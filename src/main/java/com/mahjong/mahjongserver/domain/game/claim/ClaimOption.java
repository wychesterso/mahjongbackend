package com.mahjong.mahjongserver.domain.game.claim;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ClaimOption {
    private final Decision decision;
    private final List<List<Tile>> sheungCombos; // used for Sheungs
    private final List<Tile> brightKongOptions; // used for Bright Kongs
    private final List<Tile> darkKongOptions; // used for Dark Kongs

    public ClaimOption(Decision decision, List<List<Tile>> sheungCombos,
                       List<Tile> brightKongOptions, List<Tile> darkKongOptions) {
        this.decision = decision;
        this.sheungCombos = sheungCombos;
        this.brightKongOptions = brightKongOptions;
        this.darkKongOptions = darkKongOptions;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<List<Tile>> getSheungCombos() {
        return sheungCombos;
    }

    public List<Tile> getBrightKongOptions() {
        return brightKongOptions;
    }

    public List<Tile> getDarkKongOptions() {
        return darkKongOptions;
    }
}