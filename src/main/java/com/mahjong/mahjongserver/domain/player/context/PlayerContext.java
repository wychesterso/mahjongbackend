package com.mahjong.mahjongserver.domain.player.context;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;

public class PlayerContext {
    private final Player player;
    private final PlayerDecisionHandler decisionHandler;

    public PlayerContext(Player player, PlayerDecisionHandler decisionHandler) {
        this.player = player;
        this.decisionHandler = decisionHandler;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDecisionHandler getDecisionHandler() {
        return decisionHandler;
    }
}