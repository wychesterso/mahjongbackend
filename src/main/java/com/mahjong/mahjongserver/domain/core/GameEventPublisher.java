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
                "message", message
        ));
    }

    /**
     * Send the current game state to a specified player.
     * @param playerId the id of the specified player.
     * @param tableDTO the latest representation of the game table.
     */
    public void sendTableUpdate(String playerId, Object tableDTO) {
        sendToPlayer(playerId, Map.of(
                "type", "update",
                "data", tableDTO
        ));
    }

    /**
     * Send a round-ended message to a specific player.
     * @param roomId the id of the room.
     * @param result either "draw" or "win".
     * @param data additional result info (e.g. winner seats, final table state, score).
     */
    public void sendGameEnded(String roomId, String result, Object data) {
        sendToAll(roomId, Map.of(
                "type", "round_ended",
                "result", result,
                "data", data
        ));
    }

    public void sendSessionEnded(String roomId) {
        sendToAll(roomId, Map.of(
                "type", "session_ended"
        ));
    }
}
