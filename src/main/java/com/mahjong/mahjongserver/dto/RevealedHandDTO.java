package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.hand.RevealedHand;
import com.mahjong.mahjongserver.domain.board.tile.Tile;

import java.util.List;

public class RevealedHandDTO {
    private final List<List<Tile>> groups;
    private final List<List<Tile>> brightKongs;
    private final List<List<Tile>> darkKongs;
    private final List<Tile> flowers;

    public RevealedHandDTO(RevealedHand revealedHand) {
        this.groups = revealedHand.getGroups();
        this.brightKongs = revealedHand.getBrightKongs();
        this.darkKongs = revealedHand.getDarkKongs();
        this.flowers = revealedHand.getFlowers();
    }

    public List<List<Tile>> getGroups() { return groups; }
    public List<List<Tile>> getBrightKongs() { return brightKongs; }
    public List<List<Tile>> getDarkKongs() { return darkKongs; }
    public List<Tile> getFlowers() { return flowers; }
}
