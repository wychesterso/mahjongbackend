package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.config.exception.RoomNotFoundException;
import com.mahjong.mahjongserver.domain.game.score.ScoreCalculator;
import com.mahjong.mahjongserver.domain.player.PlayerProfile;
import com.mahjong.mahjongserver.domain.player.RealPlayer;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.BotDecisionHandler;
import com.mahjong.mahjongserver.domain.player.decision.BotDecisionHandlerFactory;
import com.mahjong.mahjongserver.domain.player.decision.RealPlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.dto.mapper.DTOMapper;
import com.mahjong.mahjongserver.dto.state.RoomInfoDTO;
import com.mahjong.mahjongserver.service.PlayerProfileService;
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
    private final PlayerProfileService playerProfileService;
    private final BotDecisionHandlerFactory botFactory;

    private final Map<String, String> userToRoom = new HashMap<>();

    /**
     * Constructs a new room manager with the given configuration.
     *
     * @param eventPublisher       The publisher for game events.
     * @param playerProfileService The service for player profiles.
     */
    public RoomManager(GameEventPublisher eventPublisher,
                       PlayerProfileService playerProfileService,
                       BotDecisionHandlerFactory botFactory) {
        this.eventPublisher = eventPublisher;
        this.playerProfileService = playerProfileService;
        this.botFactory = botFactory;
    }

//============================== ROOM LIFECYCLE MANAGEMENT ==============================//

    /**
     * Creates a new room with the given ID and assigns the host to EAST.
     *
     * @param roomId          The unique identifier for the room.
     * @param scoreCalculator The scoring logic for the room.
     * @param hostId          The ID of the player hosting the room.
     * @return The created Room instance.
     * @throws IllegalStateException If the host is already in another room.
     * @throws IllegalArgumentException If the room ID is already taken.
     */
    public Room createRoom(String roomId, ScoreCalculator scoreCalculator, String hostId) {
        if (userToRoom.containsKey(hostId)) {
            throw new IllegalStateException("Player already in another room!");
        }
        if (rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room already exists: " + roomId);
        }

        PlayerProfile profile = playerProfileService.loadProfile(hostId);
        Room room = new Room(eventPublisher, roomId, scoreCalculator, new RealPlayer(profile));

        rooms.put(roomId, room);
        joinRoom(roomId, Seat.EAST, hostId);

        return room;
    }

    /**
     * Removes the room with the specified ID from the registry.
     *
     * @param roomId The ID of the room to remove.
     */
    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }

//============================== ROOM PARTICIPATION ==============================//

    /**
     * Allows a player to join a specific seat in a room.
     *
     * @param roomId   The ID of the room to join.
     * @param seat     The seat to occupy.
     * @param playerId The ID of the joining player.
     * @throws IllegalStateException If the player is already in a room or already in this room.
     */
    public void joinRoom(String roomId, Seat seat, String playerId) {
        if (userToRoom.containsKey(playerId)) {
            throw new IllegalStateException("Player already in another room!");
        }

        Room room = getRoom(roomId);

        if (room.containsPlayer(playerId)) {
            throw new IllegalStateException("Player already in this room!");
        }

        PlayerProfile profile = playerProfileService.loadProfile(playerId);
        room.addPlayer(seat, new RealPlayer(profile), new RealPlayerDecisionHandler(eventPublisher));
        userToRoom.put(playerId, roomId);
    }

    /**
     * Removes a player from a room, optionally reassigning the host or replacing with a bot.
     *
     * @param roomId   The room the player is leaving.
     * @param playerId The ID of the player exiting.
     * @throws IllegalStateException If the player is not in the specified room.
     */
    public void exitRoom(String roomId, String playerId) {
        Room room = getRoom(roomId);

        if (!room.containsPlayer(playerId)) {
            throw new IllegalStateException("Player not in room!");
        }

        userToRoom.remove(playerId);

        boolean wasHost = room.getHost().getId().equals(playerId);
        Seat seat = room.getSeat(playerId);
        room.removePlayer(playerId, botFactory.createBotDecisionHandler());

        if (room.getCurrentGame() != null && room.getCurrentGame().isActiveGame()) {
            room.addBot(seat, botFactory.createBotDecisionHandler());
        }

        if (wasHost) {
            // find next real player and assign them as host
            Optional<RealPlayer> nextReal = room.getPlayerContexts().values().stream()
                    .filter(Objects::nonNull)
                    .map(PlayerContext::getPlayer)
                    .filter(Objects::nonNull)
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

    /**
     * Switches the player's seat in the given room.
     *
     * @param roomId   The room in which to switch seats.
     * @param newSeat  The target seat to switch to.
     * @param playerId The ID of the player switching.
     * @throws IllegalStateException If the seat switch fails.
     */
    public void switchSeat(String roomId, Seat newSeat, String playerId) {
        Room room = getRoom(roomId);
        if (!room.switchSeat(playerId, newSeat)) {
            throw new IllegalStateException("Seat switch failed!");
        }
    }

    /**
     * Assigns a bot to a given seat in the room.
     *
     * @param roomId      The target room.
     * @param seat        The seat to assign the bot to.
     * @param requesterId The ID of the host making the request.
     * @throws AccessDeniedException If the requester is not the host.
     */
    public void assignBotToSeat(String roomId, Seat seat, String requesterId) {
        Room room = getRoom(roomId);
        if (!room.getHostId().equals(requesterId)) {
            throw new AccessDeniedException("Only host can add bots!");
        }
        if (!room.addBot(seat, botFactory.createBotDecisionHandler())) {
            throw new IllegalStateException("Bot could not be added!");
        }
    }

    /**
     * Generates the next ID to use for a bot.
     *
     * @param room The room to generate ID for.
     * @return The generated bot ID.
     */
    private String generateNextBotId(Room room) {
        int index = 1;
        while (true) {
            String candidateId = "bot" + index;
            if (!room.containsPlayer(candidateId)) return candidateId;
            index++;
        }
    }

    /**
     * Removes a bot from a given seat in the room.
     *
     * @param roomId      The target room.
     * @param seat        The seat to remove the bot from.
     * @param requesterId The ID of the host making the request.
     * @throws AccessDeniedException If the requester is not the host.
     */
    public void removeBotFromSeat(String roomId, Seat seat, String requesterId) {
        Room room = getRoom(roomId);
        if (!room.getHostId().equals(requesterId)) {
            throw new AccessDeniedException("Only host can remove bots!");
        }
        if (!room.removeBot(seat, botFactory.createBotDecisionHandler())) {
            throw new IllegalStateException("Bot could not be removed!");
        }
    }

//============================== ROOM ACCESS AND QUERIES ==============================//

    /**
     * Lists all active rooms and basic metadata about each.
     *
     * @return a list of maps containing roomId, host, availableSeats, and playerNames.
     */
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
     * Retrieves full DTO info for a specific room.
     *
     * @param roomId the ID of the room.
     * @return a RoomInfoDTO, or null if the room doesn't exist.
     */
    public RoomInfoDTO getRoomInfo(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return null;
        return DTOMapper.fromRoom(room);
    }

//============================== GAME MANAGEMENT ==============================//

    /**
     * Starts a game in the specified room if all seats are filled.
     *
     * @param roomId the ID of the room to start the game in.
     * @return true if the game was successfully started, false otherwise.
     */
    public boolean startGame(String roomId) {
        Room room = getRoom(roomId);
        return room.startGame();
    }
}
