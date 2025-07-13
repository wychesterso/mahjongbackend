package com.mahjong.mahjongserver.domain.player.context;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.Hand;

public class PlayerContext {
    private final Player player;
    private final Seat seat;
    private final PlayerDecisionHandler decisionHandler;
    private final Hand hand;

    public PlayerContext(Player player, Seat seat, PlayerDecisionHandler decisionHandler) {
        this.player = player;
        this.seat = seat;
        this.decisionHandler = decisionHandler;
        this.hand = new Hand();
    }

    public Player getPlayer() {
        return player;
    }

    public Seat getSeat() {
        return seat;
    }

    public PlayerDecisionHandler getDecisionHandler() {
        return decisionHandler;
    }

    public Hand getHand() {
        return hand;
    }
}