package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Map;

public class TableDTO {
    private final List<Tile> discardPile;
    private final int remainingTileCount;
    private final Map<Seat, RevealedHandDTO> revealedHands;
    private final Map<Seat, Integer> concealedTileCounts;
    private final Seat currentTurn;

    public TableDTO(List<Tile> discardPile,
                    int remainingTileCount,
                    Map<Seat, RevealedHandDTO> revealedHands,
                    Map<Seat, Integer> concealedTileCounts,
                    Seat currentTurn) {
        this.discardPile = discardPile;
        this.remainingTileCount = remainingTileCount;
        this.revealedHands = revealedHands;
        this.concealedTileCounts = concealedTileCounts;
        this.currentTurn = currentTurn;
    }

    public List<Tile> getDiscardPile() {
        return discardPile;
    }

    public int getRemainingTileCount() {
        return remainingTileCount;
    }

    public Map<Seat, RevealedHandDTO> getRevealedHands() {
        return revealedHands;
    }

    public Map<Seat, Integer> getConcealedTileCounts() {
        return concealedTileCounts;
    }

    public Seat getCurrentTurn() {
        return currentTurn;
    }
}
