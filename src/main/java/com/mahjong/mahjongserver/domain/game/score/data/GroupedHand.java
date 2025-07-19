package com.mahjong.mahjongserver.domain.game.score.data;

import com.mahjong.mahjongserver.domain.game.score.HandGrouper;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Set;

public class GroupedHand {
    private final Set<Tile> flowers;
    private final List<List<Tile>> concealedPairs;
    private final List<List<Tile>> concealedSheungs;
    private final List<List<Tile>> revealedSheungs;
    private final List<List<Tile>> concealedPongs;
    private final List<List<Tile>> revealedPongs;
    private final List<List<Tile>> brightKongs;
    private final List<List<Tile>> darkKongs;

    public GroupedHand(List<List<Tile>> grouping, Hand hand) {
        flowers = hand.getFlowers();
        concealedPairs = HandGrouper.getConcealedPairs(grouping);
        concealedSheungs = HandGrouper.getConcealedSheungs(grouping);
        revealedSheungs = hand.getSheungs();
        concealedPongs = HandGrouper.getConcealedPongs(grouping);
        revealedPongs = hand.getPongs();
        brightKongs = hand.getBrightKongs();
        darkKongs = hand.getDarkKongs();
    }

    public Set<Tile> getFlowers() {
        return flowers;
    }

    public List<List<Tile>> getConcealedPairs() {
        return concealedPairs;
    }

    public List<List<Tile>> getConcealedSheungs() {
        return concealedSheungs;
    }

    public List<List<Tile>> getRevealedSheungs() {
        return revealedSheungs;
    }

    public List<List<Tile>> getConcealedPongs() {
        return concealedPongs;
    }

    public List<List<Tile>> getRevealedPongs() {
        return revealedPongs;
    }

    public List<List<Tile>> getBrightKongs() {
        return brightKongs;
    }

    public List<List<Tile>> getDarkKongs() {
        return darkKongs;
    }
}
