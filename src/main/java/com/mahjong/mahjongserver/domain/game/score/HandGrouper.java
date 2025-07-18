package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandGrouper {

//============================== GROUPING CONCEALED TILES ==============================//

    public static List<List<List<Tile>>> getValidGroupings(List<Tile> concealedTiles) {
        List<List<List<Tile>>> groupings = new ArrayList<>();

        if (concealedTiles.size() == 17) {
            HandGrouper.getThirteenOrphansGroupings(groupings, concealedTiles);
            HandGrouper.getSixteenDisjointGroupings(groupings, concealedTiles);
            HandGrouper.getLikKuLikKuGroupings(groupings, concealedTiles);
        }
        getRegularGroupings(groupings, new ArrayList<>(concealedTiles), new ArrayList<>());

        return groupings;
    }

    private static void getRegularGroupings(List<List<List<Tile>>> groupings, List<Tile> concealedTiles,
                                     List<List<Tile>> currentGroup) {
        int numTiles = concealedTiles.size();
        if (numTiles < 2) return;

        // base case: 2 tiles left
        if (numTiles == 2) {
            if (concealedTiles.get(0) == concealedTiles.get(1)) {
                // form the pair with last 2 tiles
                List<Tile> pair = List.of(concealedTiles.get(0), concealedTiles.get(1));
                currentGroup.add(pair);
                groupings.add(new ArrayList<>(currentGroup));
                currentGroup.removeLast(); // backtrack
            }
            return;
        }

        Collections.sort(concealedTiles);
        List<List<Tile>> seenGroups = new ArrayList<>();

        // recursion: form a group of 3 then check remaining tiles
        for (int i = 0; i < numTiles - 2; i++) {
            Tile t1 = concealedTiles.get(i);

            for (int j = i + 1; j < numTiles - 1; j++) {
                Tile t2 = concealedTiles.get(j);

                for (int k = j + 1; k < numTiles; k++) {
                    Tile t3 = concealedTiles.get(k);

                    List<Tile> group = List.of(t1, t2, t3);
                    if (!seenGroups.contains(group) && HandChecker.isValidGroup(group)) {
                        currentGroup.add(group);

                        List<Tile> newConcealedTiles = new ArrayList<>(concealedTiles);
                        newConcealedTiles.remove(t1);
                        newConcealedTiles.remove(t2);
                        newConcealedTiles.remove(t3);

                        getRegularGroupings(groupings, newConcealedTiles, currentGroup);

                        currentGroup.removeLast(); // backtrack
                    }
                    seenGroups.add(group);
                }
            }
        }
    }

    private static void getThirteenOrphansGroupings(List<List<List<Tile>>> groupings, List<Tile> concealedTiles) {

    }

    private static void getSixteenDisjointGroupings(List<List<List<Tile>>> groupings, List<Tile> concealedTiles) {

    }

    private static void getLikKuLikKuGroupings(List<List<List<Tile>>> groupings, List<Tile> concealedTiles) {

    }

//============================== SORTING GROUPED TILES ==============================//

    public static List<List<Tile>> getConcealedSheungs(List<List<Tile>> grouping) {
        List<List<Tile>> concealedSheungs = new ArrayList<>();

        for (List<Tile> group : grouping) {
            if (group.size() == 3 && group.getFirst() != group.getLast()) {
                concealedSheungs.add(group);
            }
        }

        return concealedSheungs;
    }

    public static List<List<Tile>> getConcealedPongs(List<List<Tile>> grouping) {
        List<List<Tile>> concealedPongs = new ArrayList<>();

        for (List<Tile> group : grouping) {
            if (group.size() == 3 && group.getFirst() == group.getLast()) {
                concealedPongs.add(group);
            }
        }

        return concealedPongs;
    }

    public static List<List<Tile>> getConcealedPairs(List<List<Tile>> grouping) {
        List<List<Tile>> concealedPairs = new ArrayList<>();

        for (List<Tile> group : grouping) {
            if (group.size() == 2 && group.getFirst() == group.getLast()) {
                concealedPairs.add(group);
            }
        }

        return concealedPairs;
    }
}