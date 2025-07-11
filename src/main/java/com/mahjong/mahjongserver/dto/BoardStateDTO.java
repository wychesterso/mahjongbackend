package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.tile.Tile;

import java.util.*;

public class BoardStateDTO {
    private final List<Tile> discardPile;
    private final Map<String, OpponentViewDTO> opponents; // keys: "left", "across", "right"
    private final int remainingTileCount;

    public BoardStateDTO(List<Tile> discardPile, Map<String, OpponentViewDTO> opponents, int remainingTileCount) {
        this.discardPile = discardPile;
        this.opponents = opponents;
        this.remainingTileCount = remainingTileCount;
    }

    public List<Tile> getDiscardPile() {
        return discardPile;
    }

    public Map<String, OpponentViewDTO> getOpponents() {
        return opponents;
    }

    public int getRemainingTileCount() {
        return remainingTileCount;
    }
}