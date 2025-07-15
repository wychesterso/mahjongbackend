package com.mahjong.mahjongserver.domain.game.claim;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ClaimResolution {
    private final Decision decision;
    private final List<Tile> selectedSheung;

    public ClaimResolution(Decision decision, List<Tile> selectedSheung) {
        this.decision = decision;
        this.selectedSheung = selectedSheung;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<Tile> getSelectedSheung() {
        return selectedSheung;
    }
}
