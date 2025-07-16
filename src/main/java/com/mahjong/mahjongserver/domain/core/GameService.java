package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.response.DiscardResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer responsible for coordinating gameplay events, routing user actions to the game logic.
 */
@Service
public class GameService {

    private final RoomManager roomManager;

    public GameService(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    /**
     * Starts the game in the given room if all seats are filled.
     */
    public void startGame(String roomId) {
        Room room = roomManager.getRoom(roomId);
        boolean success = room.startGame();
        if (success) {
            room.getCurrentGame().startGame();  // start game loop
        } else {
            throw new IllegalStateException("Not all seats are filled in room: " + roomId);
        }
    }

    /**
     * Handles a player's response after they are prompted to discard a tile.
     */
    public void handleDiscardResponse(String roomId, String playerId, DiscardResponseDTO response) {
        Room room = roomManager.getRoom(roomId);
        Player player = findPlayerInRoom(room, playerId);
        room.getCurrentGame().handleDiscard(player, response.getTile());
    }

    public void handleClaimResponse(String roomId, String playerId, Decision decision, List<Tile> sheungCombo) {
        Room room = roomManager.getRoom(roomId);
        Player player = findPlayerInRoom(room, playerId);

        room.getCurrentGame().handleClaimResponseFromDiscard(player, decision, sheungCombo);
    }

    public void handleDrawDecision(String roomId, String playerId, Decision decision) {
        Room room = roomManager.getRoom(roomId);
        Player player = findPlayerInRoom(room, playerId);

        room.getCurrentGame().handleClaimResponseFromDraw(player, decision);
    }

    public void handleAutoDiscard(String roomId, String playerId) {
        Room room = roomManager.getRoom(roomId);
        Player player = findPlayerInRoom(room, playerId);
        room.getCurrentGame().handleAutoDiscard();
    }

    public void handleAutoPass(String roomId, String playerId) {
        Room room = roomManager.getRoom(roomId);
        Player player = findPlayerInRoom(room, playerId);
        room.getCurrentGame().handleAutoPassClaim(player);
    }

    /**
     * Locates a player in the room based on their unique ID.
     */
    private Player findPlayerInRoom(Room room, String playerId) {
        for (Seat seat : Seat.values()) {
            Player player = room.getPlayerContext(seat).getPlayer();
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        throw new IllegalArgumentException("Player not found in room: " + playerId);
    }
}