package com.mahjong.mahjongserver.dto;

public class PromptMoveDTO {
    private BoardStateDTO boardState;
    private HandDTO hand;
    private RevealedHandDTO revealedHand;

    public PromptMoveDTO(BoardStateDTO boardState, HandDTO hand, RevealedHandDTO revealedHand) {
        this.boardState = boardState;
        this.hand = hand;
        this.revealedHand = revealedHand;
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
}
