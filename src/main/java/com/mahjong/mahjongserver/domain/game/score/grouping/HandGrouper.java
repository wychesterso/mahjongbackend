package com.mahjong.mahjongserver.domain.game.score.grouping;

import com.mahjong.mahjongserver.domain.game.score.HandChecker;
import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.*;

public class HandGrouper {

//============================== GROUPING CONCEALED TILES ==============================//

    /**
     * Generates all valid groupings for the given collection of concealed tiles.
     * @param concealedTiles the list of concealed tiles to group.
     * @param winningTile the winning tile.
     * @return the list of found valid groupings.
     */
    public static List<GroupedHand> getValidGroupings(List<Tile> concealedTiles, Tile winningTile) {
        List<GroupedHand> groupings = new ArrayList<>();

        if (concealedTiles.size() == 17) {
            getThirteenOrphansGroupings(groupings, concealedTiles, winningTile);
            getSixteenDisjointGroupings(groupings, concealedTiles, winningTile);
            getLikKuLikKuGroupings(groupings, concealedTiles, winningTile);
        }

        List<Tile> newConcealedTiles = new ArrayList<>(concealedTiles);
        Collections.sort(newConcealedTiles);
        getRegularGroupings(groupings, newConcealedTiles, winningTile, new GroupedHandBuilder());

        return groupings;
    }

    /**
     * Generates all valid groupings in the standard format, with 5 melds and 1 pair.
     * @param groupings the list of found valid groupings.
     * @param concealedTiles the list of unused concealed tiles.
     * @param winningTile the winning tile, or null if already used in the builder's grouping.
     * @param builder the grouping builder.
     */
    private static void getRegularGroupings(List<GroupedHand> groupings,
                                            List<Tile> concealedTiles,
                                            Tile winningTile,
                                            GroupedHandBuilder builder) {
        int numTiles = concealedTiles.size();
        if (numTiles < 2) return;

        // base case: 2 tiles left
        if (numTiles == 2) {
            if (concealedTiles.get(0) == concealedTiles.get(1)
                    && (winningTile == null || concealedTiles.get(0) == winningTile)) {
                // form the pair with last 2 tiles
                List<Tile> pair = List.of(concealedTiles.get(0), concealedTiles.get(1));
                builder.addConcealedGroup(pair);

                boolean isWinningGroup = winningTile != null;
                if (isWinningGroup) setWinningGroup(builder, pair, MeldType.PAIR);
                groupings.add(new GroupedHand(builder)); // add new valid grouping
                if (isWinningGroup) unsetWinningGroup(builder);

                builder.backtrack();
            }
            return;
        }

        List<List<Tile>> seenGroups = new ArrayList<>();

        // recursion: form a group of 3 then check remaining tiles
        for (int i = 0; i < numTiles - 2; i++) {
            Tile t1 = concealedTiles.get(i);

            for (int j = i + 1; j < numTiles - 1; j++) {
                Tile t2 = concealedTiles.get(j);

                for (int k = j + 1; k < numTiles; k++) {
                    Tile t3 = concealedTiles.get(k);

                    List<Tile> group = List.of(t1, t2, t3);
                    if (!seenGroups.contains(group)) {
                        seenGroups.add(group);

                        // check if group is valid
                        MeldType type = HandChecker.checkGroupType(group);
                        if (type == null) continue;

                        builder.addConcealedGroup(group);
                        List<Tile> newConcealedTiles = new ArrayList<>(concealedTiles);
                        newConcealedTiles.remove(t1);
                        newConcealedTiles.remove(t2);
                        newConcealedTiles.remove(t3);

                        if (winningTile != null && group.contains(winningTile)) {
                            // option 1: use this group as winning group
                            setWinningGroup(builder, group, type);
                            getRegularGroupings(groupings, newConcealedTiles, null, builder);
                            unsetWinningGroup(builder);
                        }

                        // option 2: use this group but not as winning group
                        getRegularGroupings(groupings, newConcealedTiles, winningTile, builder);

                        // backtrack
                        builder.backtrack();
                    }
                }
            }
        }
    }

    private static void getThirteenOrphansGroupings(List<GroupedHand> groupings,
                                                    List<Tile> concealedTiles,
                                                    Tile winningTile) {

    }

    private static void getSixteenDisjointGroupings(List<GroupedHand> groupings,
                                                    List<Tile> concealedTiles,
                                                    Tile winningTile) {

    }

    private static void getLikKuLikKuGroupings(List<GroupedHand> groupings,
                                               List<Tile> concealedTiles,
                                               Tile winningTile) {

    }

    private static void setWinningGroup(GroupedHandBuilder builder, List<Tile> group, MeldType type) {
        builder.setWinningGroup(group);
        builder.setWinningGroupType(type);
    }

    private static void unsetWinningGroup(GroupedHandBuilder builder) {
        builder.setWinningGroup(null);
        builder.setWinningGroupType(null);
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

//============================== FILTERING TILES WITH GROUPS (FOR BOT DECISIONS) ==============================//

    /**
     * Creates a list of the given tiles, sorted from least to most important.
     * This information can be used to determine which tile(s) should be discarded in optimal gameplay.
     * @param tiles the tiles in the player's concealed hand.
     * @param discardedTiles the tiles in the discarded pile.
     * @return a list of sorted tiles.
     */
    public static List<Tile> getTilesToDiscard(List<Tile> tiles, List<Tile> discardedTiles) {
        List<Tile> ungroupedTiles = new ArrayList<>(tiles);
        List<Tile> groupedTiles = new ArrayList<>();
        List<Tile> discardList = new ArrayList<>(discardedTiles);
        List<Tile> returnTiles = new ArrayList<>();

        // GROUP 1: Three identical word tiles
        for (TileType type : List.of(TileType.WIND, TileType.DRAGON)) {
            groupThreeIdenticalTiles(ungroupedTiles, groupedTiles, type);
        }

        // GROUP 2: Two identical word tiles
        for (TileType type : List.of(TileType.WIND, TileType.DRAGON)) {
            groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, discardList, type);
        }

        // Remove all tiles of type WORD_WIND or WORD_DRAGON from further grouping
        for (Tile tile : ungroupedTiles) {
            if (tile.getTileType().getClassification() == TileClassification.WORD) {
                returnTiles.add(tile);
            }
        }
        ungroupedTiles.removeIf(tile -> tile.getTileType().getClassification() == TileClassification.WORD);

        // GROUP 3: Six tiles of same type with ordinals n, n+1, n+1, n+1, n+1, n+2
        groupNNNN12(ungroupedTiles, groupedTiles, 6);

        // GROUP 4: Five tiles of same type with ordinals n, n+1, n+1, n+1, n+2
        groupNNNN12(ungroupedTiles, groupedTiles, 5);

        // GROUP 5: Three identical tiles
        groupThreeIdenticalTiles(ungroupedTiles, groupedTiles, null);

        // GROUP 6: Three consecutive tiles of same type
        groupThreeConsecutiveTiles(ungroupedTiles, groupedTiles);

        // GROUP 7: Two identical tiles
        groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, discardList, null);

        // GROUP 8: Two consecutive tiles of same type
        groupTwoConsecutiveTiles(ungroupedTiles, groupedTiles);

        // GROUP 9: Two tiles of same type with ordinals n, n+2
        groupTwoTilesWithGap(ungroupedTiles, groupedTiles);

        // GROUP 10: Two identical tiles
        groupTwoIdenticalTiles(ungroupedTiles, groupedTiles, new ArrayList<>(), null);

        // decide discard order by the frequency of that tile found in discard pile
        returnTiles.sort((tile1, tile2) -> {
            int count1 = Collections.frequency(discardedTiles, tile1);
            int count2 = Collections.frequency(discardedTiles, tile2);
            return Integer.compare(count2, count1);
        });
        ungroupedTiles.sort((tile1, tile2) -> {
            int count1 = Collections.frequency(discardedTiles, tile1);
            int count2 = Collections.frequency(discardedTiles, tile2);
            return Integer.compare(count2, count1);
        });

        // return tiles to discard, sorted from least to most important
        returnTiles.addAll(ungroupedTiles);
        returnTiles.addAll(groupedTiles.reversed());
        return new ArrayList<>(returnTiles);
    }

    /**
     * Attempt to group tiles from the ungrouped list if there are three identical instances.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param type the tile type to be grouped.
     */
    private static void groupThreeIdenticalTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles, TileType type) {
        for (int i = 0; i < ungroupedTiles.size() - 2; i++) {
            Tile tile = ungroupedTiles.get(i);
            if ((type == null || tile.getTileType() == type)
                    && Collections.frequency(ungroupedTiles, tile) >= 3) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                ungroupedTiles.removeAll(Arrays.asList(tile, tile, tile));
            }
        }
    }

    /**
     * Attempt to group tiles from the ungrouped list if there are two identical instances.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param discardList the tiles already discarded during the game.
     * @param type the tile types to be grouped.
     * @ensures only forms groups when a Pong is still possible, i.e. not more than one instance of
     * the tile found in the discard list.
     */
    private static void groupTwoIdenticalTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles, List<Tile> discardList, TileType type) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile = ungroupedTiles.get(i);
            if ((type == null || tile.getTileType() == type) &&
                    Collections.frequency(ungroupedTiles, tile) >= 2 &&
                    Collections.frequency(discardList, tile) <= 1) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                ungroupedTiles.removeAll(Arrays.asList(tile, tile));
            }
        }
    }

    /**
     * Attempt to group tiles in the form n, n+1, ... , n+1, n+2 of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     * @param groupSize the size of the group.
     */
    private static void groupNNNN12(List<Tile> ungroupedTiles, List<Tile> groupedTiles, int groupSize) {
        for (int i = 0; i < ungroupedTiles.size(); i++) {
            Tile tile = ungroupedTiles.get(i);
            TileType type = tile.getTileType();
            int ord = tile.ordinal();

            long countN = ungroupedTiles.stream().filter(t -> t == tile).count();
            long countN1 = ungroupedTiles.stream().filter(t -> t.ordinal() == ord + 1 && t.getTileType() == type).count();
            long countN2 = ungroupedTiles.stream().filter(t -> t.ordinal() == ord + 2 && t.getTileType() == type).count();

            if (countN >= (groupSize - 2) && countN1 >= 3 && countN2 >= 1) {
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                groupedTiles.add(tile);
                if (groupSize == 6) groupedTiles.add(tile);

                ungroupedTiles.removeAll(Arrays.asList(tile, tile, tile));
                ungroupedTiles.removeIf(t -> t.ordinal() == ord + 1 && t.getTileType() == type);
                ungroupedTiles.removeIf(t -> t.ordinal() == ord + 2 && t.getTileType() == type);
            }
        }
    }

    /**
     * Attempt to group three consecutive tiles of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    public static void groupThreeConsecutiveTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 2; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            TileType type = tile1.getTileType();

            for (int j = i + 1; j < ungroupedTiles.size() - 1; j++) {
                Tile tile2 = ungroupedTiles.get(j);
                for (int k = j + 1; k < ungroupedTiles.size(); k++) {
                    Tile tile3 = ungroupedTiles.get(k);

                    if (tile1.getTileType() == tile2.getTileType() && tile2.getTileType() == tile3.getTileType()) {
                        int ord1 = tile1.ordinal(), ord2 = tile2.ordinal(), ord3 = tile3.ordinal();
                        if ((ord1 + 1 == ord2 && ord2 + 1 == ord3) || (ord1 + 2 == ord2 && ord2 + 2 == ord3)) {
                            groupedTiles.add(tile1);
                            groupedTiles.add(tile2);
                            groupedTiles.add(tile3);
                            ungroupedTiles.removeAll(Arrays.asList(tile1, tile2, tile3));
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempt to group two consecutive tiles of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    private static void groupTwoConsecutiveTiles(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            for (int j = i + 1; j < ungroupedTiles.size(); j++) {
                Tile tile2 = ungroupedTiles.get(j);
                if (tile1.getTileType() == tile2.getTileType() && tile1.ordinal() + 1 == tile2.ordinal()) {
                    groupedTiles.add(tile1);
                    groupedTiles.add(tile2);
                    ungroupedTiles.removeAll(Arrays.asList(tile1, tile2));
                }
            }
        }
    }

    /**
     * Attempt to group two consecutive tiles with a gap (e.g. 2 & 4, 5 & 7) of the same type.
     * Removes successfully grouped tiles and moves them into the grouped list.
     * @param ungroupedTiles the list of ungrouped tiles.
     * @param groupedTiles the list of already grouped tiles.
     */
    private static void groupTwoTilesWithGap(List<Tile> ungroupedTiles, List<Tile> groupedTiles) {
        for (int i = 0; i < ungroupedTiles.size() - 1; i++) {
            Tile tile1 = ungroupedTiles.get(i);
            for (int j = i + 1; j < ungroupedTiles.size(); j++) {
                Tile tile2 = ungroupedTiles.get(j);
                if (tile1.getTileType() == tile2.getTileType() && tile1.ordinal() + 2 == tile2.ordinal()) {
                    groupedTiles.add(tile1);
                    groupedTiles.add(tile2);
                    ungroupedTiles.removeAll(Arrays.asList(tile1, tile2));
                }
            }
        }
    }

//============================== HANDLE SHEUNGS ==============================//

    public static List<List<Tile>> getSheungCombos(List<Tile> concealedTiles, Tile tile) {
        TileType tileType = tile.getTileType();

        // can't make sheungs if not regular tile
        if (tileType.getClassification() == TileClassification.WORD
                || tileType.getClassification() == TileClassification.FLOWER) {
            return new ArrayList<>();
        }

        List<Tile> sameTypeTiles = concealedTiles
                .stream()
                .filter(t -> t.getTileType().equals(tileType))
                .toList();
        int tileOrdinal = tile.ordinal();
        List<List<Tile>> validSheungs = new ArrayList<>();

        for (int i = 0; i < sameTypeTiles.size(); i++) {
            for (int j = i + 1; j < sameTypeTiles.size(); j++) {
                Tile first = sameTypeTiles.get(i);
                Tile second = sameTypeTiles.get(j);

                if ((first.ordinal() == tileOrdinal - 2 && second.ordinal() == tileOrdinal - 1) // (x-2, x-1, x)
                        || (first.ordinal() == tileOrdinal - 1 && second.ordinal() == tileOrdinal + 1) // (x-1, x, x+1)
                        || (first.ordinal() == tileOrdinal + 1 && second.ordinal() == tileOrdinal + 2)) { // (x, x+1, x+2)
                    // add valid combo to list of valid Sheungs
                    List<Tile> validCombo = Arrays.asList(first, second, tile);
                    Collections.sort(validCombo);
                    validSheungs.add(validCombo);
                }
            }
        }
        Set<List<Tile>> uniqueGroups = new LinkedHashSet<>(validSheungs);
        return new ArrayList<>(uniqueGroups);
    }
}