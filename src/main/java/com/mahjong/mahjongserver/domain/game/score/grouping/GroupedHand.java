package com.mahjong.mahjongserver.domain.game.score.grouping;

import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupedHand {
    private Set<Tile> flowers;
    private final List<List<Tile>> revealedPairs = new ArrayList<>();
    private List<List<Tile>> revealedSheungs;
    private List<List<Tile>> revealedPongs;
    private List<List<Tile>> brightKongs;
    private List<List<Tile>> darkKongs;

    private final List<List<Tile>> concealedPairs = new ArrayList<>();
    private final List<List<Tile>> concealedSheungs = new ArrayList<>();
    private final List<List<Tile>> concealedPongs = new ArrayList<>();
    private final List<List<Tile>> weirdGroups = new ArrayList<>();

    private final List<Tile> winningGroup;
    private final MeldType winningGroupType;

    /**
     * Constructs a GroupedHand from a GroupedHandBuilder that groups concealed tiles.
     * @param builder the builder hand instance.
     */
    public GroupedHand(GroupedHandBuilder builder) {
        if (!builder.hasWinningGroup()) throw new IllegalArgumentException("[GroupedHand] builder has no winning group!");

        winningGroup = builder.getWinningGroup();
        winningGroupType = builder.getWinningGroupType();

        for (List<Tile> group : builder.getConcealedGroups()) {
            if (group.size() == 2) {
                concealedPairs.add(group);
            } else if (group.size() == 3) {
                if (group.getFirst() == group.getLast()) {
                    concealedPongs.add(group);
                } else {
                    concealedSheungs.add(group);
                }
            } else {
                weirdGroups.add(group);
            }
        }
    }

    public void populateRevealedTiles(Hand hand) {
        flowers = hand.getFlowers();
        revealedSheungs = hand.getSheungs();
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

    public List<List<Tile>> getRevealedPairs() {
        return revealedPairs;
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

    public List<List<Tile>> getWeirdGroups() {
        return weirdGroups;
    }

    public List<Tile> getWinningGroup() {
        return winningGroup;
    }

    public MeldType getWinningGroupType() {
        return winningGroupType;
    }
}
