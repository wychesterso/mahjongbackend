package com.mahjong.mahjongserver.domain.game.claim;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ClaimOption {
    private final Decision decision;
    private final List<List<Tile>> validCombos; // used for Sheungs

    public ClaimOption(Decision decision, List<List<Tile>> validCombos) {
        this.decision = decision;
        this.validCombos = validCombos;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<List<Tile>> getValidCombos() {
        return validCombos;
    }
}