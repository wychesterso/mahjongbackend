package com.mahjong.mahjongserver.game;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;

import java.util.concurrent.CompletableFuture;

public class PlayerContext {
    private final String playerId;
    private final Player player;
    private final String roomId;
    private final PlayerDecisionHandler decisionHandler;

    private CompletableFuture<Object> pendingDecision;
    private String currentPromptType;

    public PlayerContext(String playerId, Player player, String roomId, PlayerDecisionHandler decisionHandler) {
        this.playerId = playerId;
        this.player = player;
        this.roomId = roomId;
        this.decisionHandler = decisionHandler;
    }

    public void setPendingDecision(CompletableFuture<Object> future, String promptType) {
        this.pendingDecision = future;
        this.currentPromptType = promptType;
    }

    public void completeDecision(Object value) {
        if (pendingDecision != null) {
            pendingDecision.complete(value);
        }
        pendingDecision = null;
        currentPromptType = null;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getRoomId() {
        return roomId;
    }

    public PlayerDecisionHandler getDecisionHandler() {
        return decisionHandler;
    }

    public String getCurrentPromptType() {
        return currentPromptType;
    }
}