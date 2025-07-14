package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.core.GameEventPublisher;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import org.springframework.stereotype.Component;

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
    public Room createRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room already exists: " + roomId);
        }
        Room room = new Room(eventPublisher, roomId);
        rooms.put(roomId, room);
        return room;
    }

    /**
     * Returns the Room with the given ID.
     * @param roomId the unique room identifier.
     * @return the corresponding Room.
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
     * Assigns a player to a specific seat in a room.
     * @param roomId the room ID.
     * @param seat the seat to assign the player to.
     * @param player the player object.
     * @param decisionHandler the decision handler (bot or real player).
     * @return true if successfully seated, false if seat is taken.
     */
    public boolean assignPlayerToSeat(String roomId, Seat seat, Player player, PlayerDecisionHandler decisionHandler) {
        Room room = getRoom(roomId);
        return room.addPlayer(seat, player, decisionHandler);
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
