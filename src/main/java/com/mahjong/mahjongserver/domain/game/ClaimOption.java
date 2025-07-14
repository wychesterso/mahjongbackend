package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ClaimOption {
    private final Seat seat;
    private final Decision decision;
    private final List<List<Tile>> validCombos; // used for Sheungs

    public ClaimOption(Seat seat, Decision decision, List<List<Tile>> validCombos) {
        this.seat = seat;
        this.decision = decision;
        this.validCombos = validCombos;
    }

    public Seat getSeat() {
        return seat;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<List<Tile>> getValidCombos() {
        return validCombos;
    }
}