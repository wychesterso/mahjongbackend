package com.mahjong.mahjongserver.domain.player.context;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;

public class PlayerContext {
    private final Player player;
    private final PlayerDecisionHandler decisionHandler;
    private final String roomId;

    public PlayerContext(Player player, PlayerDecisionHandler decisionHandler, String roomId) {
        this.player = player;
        this.decisionHandler = decisionHandler;
        this.roomId = roomId;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDecisionHandler getDecisionHandler() {
        return decisionHandler;
    }

    public String getRoomId() {
        return roomId;
    }
}