package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.hand.Hand;
import com.mahjong.mahjongserver.domain.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class HandDTO {
    private final List<Tile> tiles;
    private final Tile lastDrawnTile;

    public HandDTO(Hand hand) {
        this.tiles = new ArrayList<>(hand.getTiles());
        this.lastDrawnTile = hand.getLastDrawnTile();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Tile getLastDrawnTile() {
        return lastDrawnTile;
    }
}