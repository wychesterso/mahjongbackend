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

    public void sendPrompt(String playerId, String type, Object data) {
        sendToPlayer(playerId, Map.of("type", type, "data", data));
    }

    public void sendLog(String roomId, String message) {
        sendToAll(roomId, Map.of("type", "log", "message", message));
    }
}
