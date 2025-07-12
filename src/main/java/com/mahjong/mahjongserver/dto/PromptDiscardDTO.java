package com.mahjong.mahjongserver.dto;

public class PromptDiscardDTO {
    private final BoardStateDTO boardState;
    private final HandDTO hand;
    private final RevealedHandDTO revealedHand;
    private final boolean noDraw;

    public PromptDiscardDTO(BoardStateDTO boardState,
                            HandDTO hand,
                            RevealedHandDTO revealedHand,
                            boolean noDraw) {
        this.boardState = boardState;
        this.hand = hand;
        this.revealedHand = revealedHand;
        this.noDraw = noDraw;
    }

    public BoardStateDTO getBoardState() {
        return boardState;
    }

    public HandDTO getHand() {
        return hand;
    }

    public RevealedHandDTO getRevealedHand() {
        return revealedHand;
    }

    public boolean isNoDraw() {
        return noDraw;
    }
}