package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.game.score.ScoreCalculator;
import com.mahjong.mahjongserver.domain.player.Bot;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.BotDecisionHandler;
import com.mahjong.mahjongserver.domain.player.decision.RealPlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for managing all active rooms in the Mahjong server.
 */
@Component
public class RoomManager {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final GameEventPublisher eventPublisher;

    public RoomManager(GameEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new room with the given ID.
     * @param roomId the unique identifier for the room.
     * @return the created Room instance.
     */
    public Room createRoom(String roomId, ScoreCalculator scoreCalculator, Player host) {
        if (rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room already exists: " + roomId);
        }
        Room room = new Room(eventPublisher, roomId, scoreCalculator, host);
        rooms.put(roomId, room);
        return room;
    }

    public void assignBotToSeat(String roomId, Seat seat, String botId, String requesterId) throws AccessDeniedException {
        Room room = getRoom(roomId);
        if (!room.getHostId().equals(requesterId)) {
            throw new AccessDeniedException("Only host can add bots!");
        }
        if (room.botIdExists(botId)) {
            throw new IllegalArgumentException("Bot ID already exists in room");
        }
        Bot bot = new Bot(botId);
        room.addPlayer(seat, bot, new BotDecisionHandler());
    }

    public void joinRoom(String roomId, Seat seat, Player realPlayer) {
        Room room = getRoom(roomId);

        if (room.containsPlayer(realPlayer.getId())) {
            throw new IllegalStateException("Player already in room!");
        }

        room.addPlayer(seat, realPlayer, new RealPlayerDecisionHandler(eventPublisher));
    }

    public void exitRoom(String roomId, Player realPlayer) {
        Room room = getRoom(roomId);
        room.removePlayer(realPlayer);
    }

    /**
     * Returns the Room with the given ID.
     * @param roomId the unique room identifier.
     * @return the corresponding Room.
     * @throws IllegalArgumentException if no Room with given ID exists.
     */
    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
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
