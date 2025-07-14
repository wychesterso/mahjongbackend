package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.*;

public class HandChecker {

//============================== ENTRY POINTS ==============================//

    /**
     * Checks if there is a valid self-draw win condition.
     * @param hand the player's current hand.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public static boolean checkWin(Hand hand) {
        List<Tile> tiles = new ArrayList<>(hand.getConcealedTiles());
        Collections.sort(tiles);

        return checkSixteenDisjoint(tiles) || checkThirteenOrphans(tiles)
                || checkLikKuLikKu(tiles) != null || canFormGroups(tiles);
    }

    /**
     * Checks if there is a valid win condition when a tile is taken from an opponent discard.
     * @param hand the player's current hand.
     * @param discardedTile the tile most recently discarded.
     * @return true iff the win condition is satisfied, false otherwise.
     */
    public static boolean checkWin(Hand hand, Tile discardedTile) {
        List<Tile> tiles = new ArrayList<>(hand.getConcealedTiles());
        tiles.add(discardedTile);
        Collections.sort(tiles);

        return checkSixteenDisjoint(tiles) || checkThirteenOrphans(tiles)
                || checkLikKuLikKu(tiles) != null || canFormGroups(tiles);
    }

    public static boolean checkDarkKong(Hand hand) {
        Map<Tile, Integer> tileCount = new HashMap<>();
        for (Tile tile : hand.getConcealedTiles()) {
            int newCount = tileCount.getOrDefault(tile, 0) + 1;
            if (newCount == 4) return true;
            tileCount.put(tile, newCount);
        }
        return false;
    }

    public static boolean checkBrightKong(Hand hand) {
        for (List<Tile> pong : hand.getPongs()) {
            Tile tile = pong.getFirst();
            if (hand.getConcealedTiles().contains(tile)) return true;
        }
        return false;
    }

    public static boolean checkBrightKong(Hand hand, Tile discardedTile) {
        int count = 0;
        for (Tile tile : hand.getConcealedTiles()) {
            if (tile == discardedTile) {
                if (++count == 3) return true;
            }
        }
        return false;
    }

    public static boolean checkPong(Hand hand, Tile discardedTile) {
        int count = 0;
        for (Tile tile : hand.getConcealedTiles()) {
            if (tile == discardedTile) {
                if (++count == 2) return true;
            }
        }
        return false;
    }

    public static boolean checkSheung(Hand hand, Tile discardedTile) {
        if (discardedTile.getTileType().getClassification() != TileClassification.REGULAR) return false;

        List<Tile> concealedTiles = hand.getConcealedTiles();

        for (int i = 0; i < concealedTiles.size() - 1; i++) {
            Tile tile1 = concealedTiles.get(i);
            if (tile1.getTileType() != discardedTile.getTileType()) continue;

            for (int j = i + 1; j < concealedTiles.size(); j++) {
                Tile tile2 = concealedTiles.get(j);
                List<Tile> group = List.of(discardedTile, tile1, tile2);
                if (isValidSheung(group)) return true;
            }
        }

        return false;
    }

    public static List<List<Tile>> getSheungCombos(Hand hand, Tile discardedTile) {
        List<Tile> concealedTiles = hand.getConcealedTiles();
        List<List<Tile>> result = new ArrayList<>();

        for (int i = 0; i < concealedTiles.size() - 1; i++) {
            Tile tile1 = concealedTiles.get(i);
            if (tile1.getTileType() != discardedTile.getTileType()) continue;

            for (int j = i + 1; j < concealedTiles.size(); j++) {
                Tile tile2 = concealedTiles.get(j);
                List<Tile> group = List.of(discardedTile, tile1, tile2);

                if (isValidSheung(group)) {
                    result.add(List.of(concealedTiles.get(i), concealedTiles.get(j)));
                }
            }
        }

        return result;
    }

//============================== GENERAL ==============================//

    /**
     * Determines whether groupings can be formed with the given set of tiles such that a win
     * condition is achieved.
     * @param tiles the list of tiles available.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean canFormGroups(List<Tile> tiles) {
        // base case: only two identical tiles
        if (tiles.size() == 2 && tiles.get(0) == tiles.get(1)) return true;

        int n = tiles.size();
        if (n < 3) return false;

        // check every combination of three tiles
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    List<Tile> group = List.of(tiles.get(i), tiles.get(j), tiles.get(k));

                    if (isValidGroup(group)) {
                        // remove group and recurse on remaining tiles
                        List<Tile> remainingTiles = new ArrayList<>(tiles);
                        remainingTiles.remove(tiles.get(k));
                        remainingTiles.remove(tiles.get(j));
                        remainingTiles.remove(tiles.get(i));

                        if (canFormGroups(remainingTiles)) return true;
                    }
                }
            }
        }

        // no valid grouping found
        return false;
    }

    /**
     * Checks if the given tiles form a valid group of 3.
     * @param tiles the tiles to check.
     * @return true iff the tiles form a valid group, false otherwise.
     */
    private static boolean isValidGroup(List<Tile> tiles) {
        return isValidPong(tiles) || isValidSheung(tiles);
    }

    /**
     * Checks if the given tiles form a valid Sheung.
     * @param tiles the tiles to check.
     * @return true iff the tiles form a valid Sheung, false otherwise.
     */
    private static boolean isValidSheung(List<Tile> tiles) {
        if (tiles.size() != 3) return false;

        Tile t1 = tiles.get(0);
        Tile t2 = tiles.get(1);
        Tile t3 = tiles.get(2);

        // check tile type
        TileType type = t1.getTileType();
        if (type.getClassification() != TileClassification.REGULAR) return false;
        if (t2.getTileType() != type || t3.getTileType() != type) return false;

        // check if tiles are adjacent using ordinals
        int o1 = t1.ordinal();
        int o2 = t2.ordinal();
        int o3 = t3.ordinal();

        int min = Math.min(o1, Math.min(o2, o3));
        int max = Math.max(o1, Math.max(o2, o3));
        int mid = o1 + o2 + o3 - min - max;

        return mid == min + 1 && max == mid + 1;
    }

    /**
     * Checks if the given tiles form a valid Pong.
     * @param tiles the tiles to check.
     * @return true iff the tiles form a valid Pong, false otherwise.
     */
    private static boolean isValidPong(List<Tile> tiles) {
        if (tiles.size() != 3) return false;

        Tile t1 = tiles.get(0);
        Tile t2 = tiles.get(1);
        Tile t3 = tiles.get(2);
        return t1 == t2 && t2 == t3;
    }

    /**
     * Checks if every tile other than one of them can form a pair of two identical tiles.
     * @param tiles the tiles to be checked.
     * @return the lone tile iff all other tiles can be paired, else null.
     */
    public static Tile checkPairs(List<Tile> tiles) {
        Map<Tile, Integer> tileCount = new HashMap<>();
        for (Tile tile : tiles) {
            tileCount.put(tile, tileCount.getOrDefault(tile, 0) + 1);
        }

        Tile loneTile = null;

        for (Map.Entry<Tile, Integer> entry : tileCount.entrySet()) {
            Tile tile = entry.getKey();
            int count = entry.getValue();

            if (count % 2 == 1) {
                if (loneTile != null) {
                    // more than one lone tile; can't pair all
                    return null;
                }
                loneTile = tile;
            }
        }

        return loneTile;
    }

//============================== SIXTEEN DISJOINT ==============================//

    /**
     * Checks if the given tiles can form the Sixteen Disjoint win condition.
     * @param tiles the list of concealed tiles.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean checkSixteenDisjoint(List<Tile> tiles) {
        int n = tiles.size();
        if (n != 17) {
            return false;
        }

        // this determines if a single pair of two identical tiles is found
        boolean pairFlag = false;

        // check every combination of two tiles
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Tile tile1 = tiles.get(i);
                Tile tile2 = tiles.get(j);

                // check for two related tiles of same type
                TileType tileType = tile1.getTileType();
                if (tileType == tile2.getTileType() && tileType.getClassification() == TileClassification.REGULAR) {
                    int ord1 = tile1.ordinal();
                    int ord2 = tile2.ordinal();

                    if ((ord2 == ord1 + 1) || (ord2 == ord1 + 2)) {
                        return false;
                    }
                }

                // check for two identical tiles
                if (tile1 == tile2) {
                    // checks if there is more than one pair
                    if (pairFlag) {
                        return false;
                    }
                    // pair is found so set flag to true
                    pairFlag = true;
                }
            }
        }

        // must have exactly one pair to win (although at this point it should be satisfied anyway)
        return pairFlag;
    }

//============================== THIRTEEN ORPHANS ==============================//

    /**
     * Checks if the given tiles can form the Thirteen Orphans win condition.
     * @param tiles the list of concealed tiles.
     * @return true iff a win condition is found, false otherwise.
     */
    public static boolean checkThirteenOrphans(List<Tile> tiles) {
        // all tiles must be concealed
        if (tiles.size() != 17) return false;

        // the tiles that form a Thirteen Orphans hand
        List<Tile> thirteenOrphans = getThirteenOrphans();
        List<Tile> remainingTiles = new ArrayList<>(tiles);

        // remove the 13 orphan tiles
        for (Tile tile : thirteenOrphans) {
            if (!remainingTiles.remove(tile)) return false;
        }

        // try all combinations of 3
        for (int i = 0; i < 4; i++) {
            List<Tile> group = new ArrayList<>();
            Tile loneTile = null;

            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    loneTile = remainingTiles.get(j);
                } else {
                    group.add(remainingTiles.get(j));
                }
            }

            // if isValidGroup && loneTile in thirteenOrphans, return true
        }

        return false;

//        List<List<Tile>> groups = new ArrayList<>();
//
//        // check every combination of three tiles
//        for (int i = 0; i < 4; i++) {
//            for (int j = i + 1; j < 4; j++) {
//                for (int k = j + 1; k < 4; k++) {
//                    Tile tile1 = remainingTiles.get(i);
//                    Tile tile2 = remainingTiles.get(j);
//                    Tile tile3 = remainingTiles.get(k);
//
//                    // check for three identical tiles
//                    if (tile1 == tile2 && tile2 == tile3) {
//                        groups.add(new ArrayList<>(List.of(tile1, tile2, tile3)));
//                    }
//
//                    // check for three consecutive tiles of same type
//                    if (tile1.getTileType() == tile2.getTileType()
//                            && tile2.getTileType() == tile3.getTileType()) {
//                        int ord1 = tile1.ordinal();
//                        int ord2 = tile2.ordinal();
//                        int ord3 = tile3.ordinal();
//
//                        if ((ord2 == ord1 + 1 && ord3 == ord2 + 1)
//                                || (ord3 == ord1 + 1 && ord2 == ord3 + 1)
//                                || (ord1 == ord2 + 1 && ord3 == ord1 + 1)) {
//                            groups.add(new ArrayList<>(List.of(tile1, tile2, tile3)));
//                        }
//                    }
//                }
//            }
//        }
//
//        // checks if there are valid groups
//        if (groups.isEmpty()) {
//            return false;
//        }
//
//        // for every group, check if the tile not used is one of the required tiles
//        for (List<Tile> group : groups) {
//            List<Tile> leftoverTiles = new ArrayList<>(remainingTiles);
//            for (Tile tile : group) {
//                leftoverTiles.remove(tile);
//            }
//            if (thirteenOrphans.contains(leftoverTiles.getFirst())) {
//                return true;
//            }
//        }
//        return false;
    }

    /**
     * Retrieves a list of the 13 tiles associated with the Thirteen Orphans win condition.
     * @return the list of 13 tiles.
     */
    public static List<Tile> getThirteenOrphans() {
        return List.of(Tile.MILLION_1, Tile.MILLION_9, Tile.BAMBOO_1, Tile.BAMBOO_9,
                Tile.CIRCLE_1, Tile.CIRCLE_9, Tile.EAST, Tile.SOUTH, Tile.WEST,
                Tile.NORTH, Tile.RED_DRAGON, Tile.GREEN_DRAGON, Tile.WHITE_DRAGON);
    }

//============================== LIK KU LIK KU ==============================//

    /**
     * Checks if the given tiles can form the Lik Ku Lik Ku win condition.
     * @param tiles the list of concealed tiles.
     * @return the tile used to form the group of 3, or null if the win condition cannot be formed.
     */
    public static Tile checkLikKuLikKu(List<Tile> tiles) {
        int n = tiles.size();
        if (n != 17) {
            return null;
        }

        Tile tile = checkPairs(tiles);
        if (tile == null) {
            return null;
        }
        List<Tile> remainingTiles = new ArrayList<>(tiles);
        remainingTiles.remove(tile);
        remainingTiles.remove(tile);
        if (remainingTiles.remove(tile)) {
            return tile;
        }
        return null;
    }
}
