package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.config.exception.RoomNotFoundException;
import com.mahjong.mahjongserver.domain.game.score.ScoreCalculator;
import com.mahjong.mahjongserver.domain.player.Bot;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.RealPlayer;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.BotDecisionHandler;
import com.mahjong.mahjongserver.domain.player.decision.RealPlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.dto.mapper.DTOMapper;
import com.mahjong.mahjongserver.dto.state.RoomInfoDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for managing all active rooms in the Mahjong server.
 */
@Component
public class RoomManager {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final GameEventPublisher eventPublisher;

    private final Map<String, String> userToRoom = new HashMap<>();

    public RoomManager(GameEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new room with the given ID.
     * @param roomId the unique identifier for the room.
     * @return the created Room instance.
     */
    public Room createRoom(String roomId, ScoreCalculator scoreCalculator, Player host) {
        if (userToRoom.containsKey(host.getId())) {
            throw new IllegalStateException("Player already in another room!");
        }
        if (rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room already exists: " + roomId);
        }
        Room room = new Room(eventPublisher, roomId, scoreCalculator, host);
        rooms.put(roomId, room);
        joinRoom(roomId, Seat.EAST, host);
        return room;
    }

    public void assignBotToSeat(String roomId, Seat seat, String requesterId) {
        Room room = getRoom(roomId);
        if (!room.getHostId().equals(requesterId)) {
            throw new AccessDeniedException("Only host can add bots!");
        }
        Bot bot = new Bot(generateNextBotId(room));
        room.addPlayer(seat, bot, new BotDecisionHandler());
    }

    private String generateNextBotId(Room room) {
        int index = 1;
        while (true) {
            String candidateId = "bot" + index;
            if (!room.containsPlayer(candidateId)) return candidateId;
            index++;
        }
    }

    public void joinRoom(String roomId, Seat seat, Player realPlayer) {
        if (userToRoom.containsKey(realPlayer.getId())) {
            throw new IllegalStateException("Player already in another room!");
        }

        Room room = getRoom(roomId);

        if (room.containsPlayer(realPlayer.getId())) {
            throw new IllegalStateException("Player already in this room!");
        }

        room.addPlayer(seat, realPlayer, new RealPlayerDecisionHandler(eventPublisher));
        userToRoom.put(realPlayer.getId(), roomId);
    }

    public void exitRoom(String roomId, Player realPlayer) {
        Room room = getRoom(roomId);

        if (!room.containsPlayer(realPlayer.getId())) {
            throw new IllegalStateException("Player not in room!");
        }

        userToRoom.remove(realPlayer.getId());

        boolean wasHost = room.getHost().getId().equals(realPlayer.getId());
        Seat seat = room.getSeat(realPlayer);
        room.removePlayer(realPlayer);
        if (room.getCurrentGame().isActiveGame()) {
            Bot bot = new Bot(generateNextBotId(room));
            room.addPlayer(seat, bot, new BotDecisionHandler());
        }

        if (wasHost) {
            // find next real player and assign them as host
            Optional<RealPlayer> nextReal = room.getPlayerContexts().values().stream()
                    .map(PlayerContext::getPlayer)
                    .filter(p -> p instanceof RealPlayer)
                    .map(p -> (RealPlayer) p)
                    .findFirst();

            if (nextReal.isPresent()) {
                room.setHost(nextReal.get());
            } else {
                // no more real players - close room
                removeRoom(roomId);
            }
        }
    }

    public List<Map<String, Object>> getAllRoomsInfo() {
        List<Map<String, Object>> roomList = new ArrayList<>();

        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomId", entry.getKey());
            roomInfo.put("host", room.getHostId());
            roomInfo.put("availableSeats", room.getNumEmptySeats());
            roomInfo.put("playerNames", room.getPlayerNames());
            roomList.add(roomInfo);
        }

        return roomList;
    }

    public RoomInfoDTO getRoomInfo(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return null;
        return DTOMapper.fromRoom(room);
    }

    /**
     * Returns the Room with the given ID.
     * @param roomId the unique room identifier.
     * @return the corresponding Room.
     * @throws RoomNotFoundException if no Room with given ID exists.
     */
    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room not found: " + roomId);
        }
        return room;
    }

    /**
     * Removes the room with the specified ID from the registry.
     * @param roomId the ID of the room to remove.
     */
    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }

    /**
     * Starts a game in the specified room if all players are seated.
     * @param roomId the room to start the game in.
     * @return true if the game started successfully, false if not all seats are filled.
     */
    public boolean startGame(String roomId) {
        Room room = getRoom(roomId);
        return room.startGame();
    }
}
