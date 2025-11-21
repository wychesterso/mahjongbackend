package com.mahjong.mahjongserver.domain.core;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public GameEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToPlayer(String playerId, Object payload) {
        messagingTemplate.convertAndSendToUser(playerId, "/queue/game", payload);
    }

    public void sendToAll(String roomId, Object payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, payload);
    }

    /**
     * Send a prompt for action to a specified player.
     * @param playerId the id of the specified player.
     * @param type the prompt type.
     * @param data the prompt data.
     */
    public void sendPrompt(String playerId, String type, Object data) {
        sendToPlayer(playerId, Map.of(
                "type", type,
                "data", data
        ));
    }

    /**
     * Send a broadcast message to all players.
     * @param roomId the id of the room.
     * @param message the message to broadcast.
     */
    public void sendLog(String roomId, String message) {
        sendToAll(roomId, Map.of(
                "type", "log",
                "data", message
        ));
    }

    /**
     * Send a game-started message to all players in the room.
     * @param roomId the id of the room.
     */
    public void sendGameStart(String roomId) {
        sendToAll(roomId, Map.of(
                "type", "game_start",
                "data", Map.of()
        ));
    }

    /**
     * Send the current game state to a specified player.
     * @param playerId the id of the specified player.
     * @param gameStateDTO the latest representation of the game state.
     */
    public void sendTableUpdate(String playerId, Object gameStateDTO) {
        // attempt to log gameActive if possible
        try {
            if (gameStateDTO != null && gameStateDTO.getClass().getSimpleName().equals("GameStateDTO")) {
                // reflectively try to read gameActive (record accessor)
                Object active = gameStateDTO.getClass().getMethod("gameActive").invoke(gameStateDTO);
                System.out.println("[GameEventPublisher] sendTableUpdate -> player=" + playerId + ", gameActive=" + active);
            }
        } catch (Exception e) {
            // fallthrough
            System.out.println("[GameEventPublisher] sendTableUpdate -> player=" + playerId + ", (could not read gameActive)");
        }

        sendToPlayer(playerId, Map.of(
                "type", "update",
                "data", gameStateDTO
        ));
    }

    /**
     * Send a game-ended message to all players in the room.
     * @param roomId the id of the room.
     * @param data additional result info (e.g. winner seats, final table state, score).
     */
    public void sendGameEnd(String roomId, Object data) {
        sendToAll(roomId, Map.of(
                "type", "game_end",
                "data", data
        ));
    }

    public void sendSessionEnded(String roomId) {
        sendToAll(roomId, Map.of(
                "type", "session_ended",
                "data", Map.of()
        ));
    }
}
