package com.mahjong.mahjongserver.websocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
        // Add session to some session pool if needed
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        // Handle message (e.g., dispatch to TurnManager or GameService)
        session.sendMessage(new TextMessage("ACK: " + payload));
    }
}
