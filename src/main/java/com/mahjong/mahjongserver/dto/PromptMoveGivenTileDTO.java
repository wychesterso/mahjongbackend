package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.tile.Tile;

public class PromptMoveGivenTileDTO {
    private BoardStateDTO boardState;
    private Tile tile;
    private HandDTO hand;
    private RevealedHandDTO revealedHand;

    public PromptMoveGivenTileDTO(BoardStateDTO boardState, Tile tile, HandDTO hand, RevealedHandDTO revealedHand) {
        this.boardState = boardState;
        this.tile = tile;
        this.hand = hand;
        this.revealedHand = revealedHand;
    }

    public BoardStateDTO getBoardState() {
        return boardState;
    }

    public Tile getTile() {
        return tile;
    }

    public HandDTO getHand() {
        return hand;
    }

    public RevealedHandDTO getRevealedHand() {
        return revealedHand;
    }
}
