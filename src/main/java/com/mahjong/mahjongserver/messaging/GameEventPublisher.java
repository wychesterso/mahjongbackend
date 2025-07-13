package com.mahjong.mahjongserver.messaging;

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
        sendToPlayer(playerId, Map.of("type", type, "data", data));
    }

    /**
     * Send a broadcast message to all players.
     * @param roomId the id of the current room.
     * @param message the message to broadcast.
     */
    public void sendLog(String roomId, String message) {
        sendToAll(roomId, Map.of("type", "log", "message", message));
    }



    public void sendGameSummary(String roomId) {

    }

    public void sendRoomSummary(String roomId) {

    }
}
