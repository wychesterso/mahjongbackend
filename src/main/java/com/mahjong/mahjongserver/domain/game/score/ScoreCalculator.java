package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.room.Seat;

public interface ScoreCalculator {
    ScoringContext calculateScore(Game game, Seat seat);


//    private int calculateScore(List<List<Tile>> highestHand, List<Score> highestPoints,
//                               Seat gameSeat, Seat roundSeat, Game turnManager,
//                               Hand winnerHandManager, Seat winnerSeat, Seat loserSeat,
//                               boolean multipleWinners, int lumZhongNum,
//                               String lastEvent, int discardCount) {
//        Tile wonOffTile = winnerHandManager.getHand().getLastDrawnTile();
//
//        int highestScore = 0;
//        for (List<List<Tile>> hand : ScoreCalculator.getValidHands(winnerHandManager.getHand().getTiles())) {
//            List<Score> points = ScoreCalculator.getPoints(gameSeat, roundSeat,
//                    winnerSeat, loserSeat,
//                    new ArrayList<>(hand), wonOffTile, winnerHandManager,
//                    turnManager.getPileManager().getUnrevealedPile().getRemainingTileCount(),
//                    turnManager.getPileManager().getDiscardPile().getDiscardedTiles(),
//                    multipleWinners, lumZhongNum, lastEvent, discardCount);
//            int score = ScoreCalculator.getScore(points);
//            if (score > highestScore) {
//                highestScore = score;
//                highestHand.clear();
//                highestHand.addAll(hand);
//                highestPoints.clear();
//                highestPoints.addAll(points);
//            }
//        }
//        Collections.sort(highestPoints);
//        return highestScore;
//    }
//
//    /**
//     * Retrieves every possible unique combination of groups that can be made from the given tiles.
//     * @param hand the list of tiles.
//     * @return a list of every possible hand, which contains a list of groups of 2 or 3 tiles.
//     */
//    public static List<List<List<Tile>>> getValidHands(List<Tile> hand) {
//        List<Tile> tileList = new ArrayList<>(hand);
//        Set<List<List<Tile>>> allValidHands = new HashSet<>();
//
//        if (WinChecker.checkSixteenDisjoint(tileList)) {
//            List<Tile> remainingTiles = new ArrayList<>(tileList);
//            List<Tile> disjointGroup = new ArrayList<>();
//            List<Tile> pairGroup = new ArrayList<>();
//            for (Tile tile : tileList) {
//                boolean oneInstance = remainingTiles.remove(tile);
//                if (oneInstance && remainingTiles.remove(tile)) {
//                    pairGroup.add(tile);
//                    pairGroup.add(tile);
//                } else if (oneInstance) {
//                    disjointGroup.add(tile);
//                }
//            }
//            List<List<Tile>> validHand = new ArrayList<>();
//            validHand.add(disjointGroup);
//            validHand.add(pairGroup);
//            return List.of(validHand);
//
//        } else if (WinChecker.checkThirteenOrphans(tileList)) {
//            List<Tile> orphanGroup = new ArrayList<>();
//            for (Tile tile : WinChecker.getThirteenOrphans()) {
//                tileList.remove(tile);
//                orphanGroup.add(tile);
//            }
//
//            List<Tile> normalGroup = new ArrayList<>();
//            List<Tile> pairGroup = new ArrayList<>();
//
//            for (int i = 0; i < 4; i++) {
//                for (int j = i + 1; j < 4; j++) {
//                    for (int k = j + 1; k < 4; k++) {
//                        Tile tile1 = tileList.get(i);
//                        Tile tile2 = tileList.get(j);
//                        Tile tile3 = tileList.get(k);
//
//                        // check for three identical tiles
//                        if (tile1 == tile2 && tile2 == tile3) {
//                            List<Tile> remainingTiles = new ArrayList<>(tileList);
//                            remainingTiles.remove(tile1);
//                            remainingTiles.remove(tile2);
//                            remainingTiles.remove(tile3);
//
//                            Tile lastTile = remainingTiles.getFirst();
//                            if (WinChecker.getThirteenOrphans().contains(lastTile)) {
//                                orphanGroup.remove(lastTile);
//                                normalGroup = List.of(tile1, tile2, tile3);
//                                pairGroup = List.of(lastTile, lastTile);
//                            }
//                        }
//
//                        // check for three consecutive tiles of same type
//                        if (tile1.getTileType() == tile2.getTileType()
//                                && tile2.getTileType() == tile3.getTileType()) {
//                            int ord1 = tile1.ordinal();
//                            int ord2 = tile2.ordinal();
//                            int ord3 = tile3.ordinal();
//
//                            if ((ord2 == ord1 + 1 && ord3 == ord2 + 1)
//                                    || (ord3 == ord1 + 1 && ord2 == ord3 + 1)
//                                    || (ord1 == ord2 + 1 && ord3 == ord1 + 1)) {
//                                List<Tile> remainingTiles = new ArrayList<>(tileList);
//                                remainingTiles.remove(tile1);
//                                remainingTiles.remove(tile2);
//                                remainingTiles.remove(tile3);
//
//                                Tile lastTile = remainingTiles.getFirst();
//                                if (WinChecker.getThirteenOrphans().contains(lastTile)) {
//                                    orphanGroup.remove(lastTile);
//                                    normalGroup = List.of(tile1, tile2, tile3);
//                                    pairGroup = List.of(lastTile, lastTile);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            List<List<Tile>> validHand = new ArrayList<>();
//            validHand.add(orphanGroup);
//            validHand.add(normalGroup);
//            validHand.add(pairGroup);
//            return List.of(validHand);
//
//        } else {
//            Tile extraTile = WinChecker.checkLikKuLikKu(tileList);
//            if (extraTile != null) {
//                List<List<Tile>> validGroups = new ArrayList<>();
//                List<Tile> remainingTiles = new ArrayList<>(hand);
//                remainingTiles.remove(extraTile);
//                remainingTiles.remove(extraTile);
//                remainingTiles.remove(extraTile);
//                List<Tile> uniqueTiles = new ArrayList<>(new HashSet<>(remainingTiles));
//                List<Tile> pongGroup = List.of(extraTile, extraTile, extraTile);
//                validGroups.add(pongGroup);
//                for (Tile tile : uniqueTiles) {
//                    while (remainingTiles.contains(tile)) {
//                        List<Tile> pairGroup = List.of(tile, tile);
//                        validGroups.add(pairGroup);
//                        remainingTiles.remove(tile);
//                        remainingTiles.remove(tile);
//                    }
//                }
//                allValidHands.add(validGroups);
//            }
//        }
//
//        findValidHands(tileList, new ArrayList<>(), allValidHands);
//        return new ArrayList<>(allValidHands);
//    }
//
//
//    /**
//     * Retrieves every possible unique combination of groups that can be formed from the given
//     * tiles, storing them in allPossibleHands.
//     * @param tiles the list of tiles to form groups with.
//     * @param currentHand a list of groups already formed. In the first iteration,
//     *                    simply pass in null value or an empty resizable list.
//     * @param allPossibleHands the set containing all possible hands.
//     */
//    private static void findValidHands(List<Tile> tiles, List<List<Tile>> currentHand,
//                                       Set<List<List<Tile>>> allPossibleHands) {
//
//        if (currentHand == null) {
//            currentHand = new ArrayList<>();
//        }
//
//        // base case: if there are only 2 tiles left, check if they form a pair
//        if (tiles.size() == 2 && tiles.get(0) == tiles.get(1)) {
//            List<Tile> pair = new ArrayList<>(tiles);
//            // make a new list before sorting to avoid interfering with backtracking
//            List<List<Tile>> sortedCurrentHand = new ArrayList<>(currentHand);
//            sortedCurrentHand.add(pair);  // add pair to current hand
//            HandSorter.sortLists(sortedCurrentHand);
//            allPossibleHands.add(new ArrayList<>(sortedCurrentHand));
//            return;
//        }
//
//        int tilesSize = tiles.size();
//
//        // Check every combination of three tiles
//        for (int i = 0; i < tilesSize; i++) {
//            for (int j = i + 1; j < tilesSize; j++) {
//                for (int k = j + 1; k < tilesSize; k++) {
//                    Tile tile1 = tiles.get(i);
//                    Tile tile2 = tiles.get(j);
//                    Tile tile3 = tiles.get(k);
//
//                    // check for Pongs
//                    if (tile1 == tile2 && tile2 == tile3) {
//                        List<Tile> group = List.of(tile1, tile2, tile3);
//                        // list of unused tiles
//                        List<Tile> remainingTiles = new ArrayList<>(tiles);
//                        remainingTiles.remove(tile1);
//                        remainingTiles.remove(tile2);
//                        remainingTiles.remove(tile3);
//                        currentHand.add(group);
//                        // recursive call to find possible hands
//                        findValidHands(remainingTiles, currentHand, allPossibleHands);
//                        currentHand.removeLast(); // backtrack
//                    }
//                }
//            }
//        }
//
//        // Check every combination of three tiles
//        for (int i = 0; i < tilesSize; i++) {
//            for (int j = i + 1; j < tilesSize; j++) {
//                for (int k = j + 1; k < tilesSize; k++) {
//                    Tile tile1 = tiles.get(i);
//                    Tile tile2 = tiles.get(j);
//                    Tile tile3 = tiles.get(k);
//
//                    // check for Sheungs
//                    if (tile1.getTileType() == tile2.getTileType() && tile2.getTileType() == tile3.getTileType()) {
//                        int ord1 = tile1.ordinal();
//                        int ord2 = tile2.ordinal();
//                        int ord3 = tile3.ordinal();
//
//                        if (ord2 == ord1 + 1 && ord3 == ord2 + 1) {
//                            List<Tile> group = List.of(tile1, tile2, tile3);
//                            List<Tile> remainingTiles = new ArrayList<>(tiles);
//                            remainingTiles.remove(tile1);
//                            remainingTiles.remove(tile2);
//                            remainingTiles.remove(tile3);
//                            currentHand.add(group);
//                            findValidHands(remainingTiles, currentHand, allPossibleHands);
//                            currentHand.removeLast();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static List<Score> getPoints(Seat gameSeat, Seat roundSeat, Seat playerSeat,
//                                               Seat loserSeat, List<List<Tile>> hand,
//                                               Tile wonOffTile, HandManager handManager,
//                                               int numUnrevealedTiles, List<Tile> discardPile,
//                                               boolean multipleWinners, int lumZhongNum,
//                                               String lastEvent, int discardCount) {
//
//        // Initialize hands
//        RevealedHand revealed = handManager.getRevealedHand();
//        List<List<Tile>> revealedHand = new ArrayList<>(revealed.getGroups());
//        List<List<Tile>> brightKongs = new ArrayList<>(revealed.getBrightKongs());
//        List<List<Tile>> darkKongs = new ArrayList<>(revealed.getDarkKongs());
//        List<Tile> flowers = new ArrayList<>(revealed.getFlowers());
//
//        // Initialize output list
//        List<Score> points = new ArrayList<>();
//
//        // Determine player's direction in the game
//        int playerGameSeatOrdinal = (playerSeat.ordinal() - roundSeat.ordinal() + 4) % 4;
//        Seat playerGameSeat = Seat.values()[playerGameSeatOrdinal];
//
//        // Sort all hands for convenience
//        HandSorter.sortLists(hand);
//        HandSorter.sortLists(revealedHand);
//        HandSorter.sortLists(brightKongs);
//        HandSorter.sortLists(darkKongs);
//
//        // Standard lists
//        List<TileType> nonWordTypes = List.of(TileType.TUNG, TileType.SOK, TileType.MAAN);
//        List<TileType> wordTypes = List.of(TileType.WORD_WIND, TileType.WORD_DRAGON);
//        List<TileType> nonFlowerTypes = new ArrayList<>();
//        nonFlowerTypes.addAll(nonWordTypes);
//        nonFlowerTypes.addAll(wordTypes);
//        List<TileType> flowerTypes = List.of(TileType.FLOWER_SEASON, TileType.FLOWER_PLANT);
//
//        // Create reference lists of different group types with different revealed status
//        List<List<Tile>> brightSheungs = new ArrayList<>();  // revealed Sheungs
//        List<List<Tile>> darkSheungs = new ArrayList<>();    // unrevealed Sheungs
//        List<List<Tile>> brightPongs = new ArrayList<>();    // revealed Pongs
//        List<List<Tile>> darkPongs = new ArrayList<>();      // unrevealed Pongs
//        List<Tile> pair = null;                              // final pair of two
//
//        for (List<Tile> group : hand) {
//            if (group.size() == 2) {
//                pair = group;
//            } else if (group.get(0) == group.get(1)) {
//                darkPongs.add(group);
//            } else {
//                darkSheungs.add(group);
//            }
//        }
//
//        for (List<Tile> group : revealedHand) {
//            if (group.get(0) == group.get(1)) {
//                brightPongs.add(group);
//            } else {
//                brightSheungs.add(group);
//            }
//        }
//
//        // Create combined reference lists of different group types
//        int numDarkSheungs = darkSheungs.size();
//        List<List<Tile>> brights = new ArrayList<>();
//        brights.addAll(brightSheungs);
//        brights.addAll(brightPongs);
//        brights.addAll(brightKongs);
//
//        List<List<Tile>> darks = new ArrayList<>();
//        darks.addAll(darkSheungs);
//        darks.addAll(darkPongs);
//        darks.addAll(darkKongs);
//
//        List<List<Tile>> sheungs = new ArrayList<>();
//        sheungs.addAll(brightSheungs);
//        sheungs.addAll(darkSheungs);
//        int numSheungs = sheungs.size();
//
//        List<List<Tile>> pongs = new ArrayList<>();
//        pongs.addAll(brightPongs);
//        pongs.addAll(darkPongs);
//
//        List<List<Tile>> kongs = new ArrayList<>();
//        kongs.addAll(brightKongs);
//        kongs.addAll(darkKongs);
//
//        List<List<Tile>> pongsAndKongs = new ArrayList<>();
//        pongsAndKongs.addAll(pongs);
//        pongsAndKongs.addAll(kongs);
//        int numPongsAndKongs = pongsAndKongs.size();
//        Tile pairTile = pair.getFirst();
//
//        List<List<Tile>> allGroups = new ArrayList<>();
//        allGroups.addAll(sheungs);
//        allGroups.addAll(pongsAndKongs);
//        allGroups.add(pair);
//
//        // Sort reference lists
//        HandSorter.sortLists(sheungs);
//        HandSorter.sortPongs(pongsAndKongs);
//
//        // Create combined reference lists of different tile types
//        List<List<Tile>> tungs = new ArrayList<>();
//        List<List<Tile>> soks = new ArrayList<>();
//        List<List<Tile>> maans = new ArrayList<>();
//        List<List<Tile>> winds = new ArrayList<>();
//        List<List<Tile>> dragons = new ArrayList<>();
//        for (List<Tile> group : allGroups) {
//            if (group.getFirst().getTileType() == TileType.TUNG) {
//                tungs.add(group);
//            } else if (group.getFirst().getTileType() == TileType.SOK) {
//                soks.add(group);
//            } else if (group.getFirst().getTileType() == TileType.MAAN) {
//                maans.add(group);
//            } else if (group.getFirst().getTileType() == TileType.WORD_WIND) {
//                winds.add(group);
//            } else if (group.getFirst().getTileType() == TileType.WORD_DRAGON) {
//                dragons.add(group);
//            }
//        }
//        int numFlowerSeason = 0;
//        int numFlowerPlant = 0;
//
//
//        // ------------------------------ POINTS ADDED BELOW ------------------------------
//
//
//        // WEIRD HANDS (十三么,十六不搭,嚦咕嚦咕)
//        if (hand.size() == 8) {
//            points.add(Score.LIK_KU_LIK_KU);
//        } else for (List<Tile> group : hand) {
//            if (group.size() == 15) {
//                points.add(Score.SAP_LUK_BAT_DAP);
//            } else if (group.size() == 12) {
//                points.add(Score.SAP_SAM_YIU);
//            }
//        }
//
//        // SELF-DRAW (自摸)
//        if (playerSeat == loserSeat) {
//            points.add(Score.SELF_DRAW);
//        }
//
//
//        // MULTIPLE WINNERS (雙響)
//        if (multipleWinners) {
//            points.add(Score.MULTIPLE_WINNERS);
//        }
//
//
//        // FLOWER TILES (花)
//        if (flowers.isEmpty()) {
//            points.add(Score.MO_FA);
//        } else {
//            for (Tile flowerTile : flowers) {
//                if (flowerTile.getTileType() == TileType.FLOWER_SEASON) {
//                    numFlowerSeason += 1;
//                } else if (flowerTile.getTileType() == TileType.FLOWER_PLANT) {
//                    numFlowerPlant += 1;
//                }
//                if (flowerTile.getTileNum() % 4 == (playerGameSeat.ordinal() + 1) % 4) {
//                    points.add(Score.ZENG_FA);
//                } else {
//                    points.add(Score.LAN_FA);
//                }
//            }
//            if (numFlowerSeason == 4 && numFlowerPlant == 4) {
//                // TWO COMPLETE SETS OF FLOWER TILES (兩台花)
//                for (int i = 0; i < 2; i++) {
//                    points.remove(Score.ZENG_FA);
//                }
//                for (int i = 0; i < 6; i++) {
//                    points.remove(Score.LAN_FA);
//                }
//                points.add(Score.LEUNG_TOI_FA);
//            } else if (numFlowerSeason == 4 || numFlowerPlant == 4) {
//                // ONE COMPLETE SET OF FLOWER TILES (一台花)
//                points.remove(Score.ZENG_FA);
//                for (int i = 0; i < 3; i++) {
//                    points.remove(Score.LAN_FA);
//                }
//                points.add(Score.YAT_TOI_FA);
//            }
//        }
//
//
//        // WIND TILES (東南西北, 三風四喜)
//        List<Score> windPoints = new ArrayList<>();
//        for (List<Tile> group : pongsAndKongs) {
//            if (group.getFirst() == Tile.WIND_EAST) {
//                Score windPoint = Score.EAST;
//                if (playerGameSeat == Seat.EAST) {
//                    windPoint.addPointScore(1);
//                }
//                if (gameSeat == Seat.EAST) {
//                    windPoint.addPointScore(1);
//                }
//                windPoints.add(windPoint);
//            } else if (group.getFirst() == Tile.WIND_SOUTH) {
//                Score windPoint = Score.SOUTH;
//                if (playerGameSeat == Seat.SOUTH) {
//                    windPoint.addPointScore(1);
//                }
//                if (gameSeat == Seat.SOUTH) {
//                    windPoint.addPointScore(1);
//                }
//                windPoints.add(windPoint);
//            } else if (group.getFirst() == Tile.WIND_WEST) {
//                Score windPoint = Score.WEST;
//                if (playerGameSeat == Seat.WEST) {
//                    windPoint.addPointScore(1);
//                }
//                if (gameSeat == Seat.WEST) {
//                    windPoint.addPointScore(1);
//                }
//                windPoints.add(windPoint);
//            } else if (group.getFirst() == Tile.WIND_NORTH) {
//                Score windPoint = Score.NORTH;
//                if (playerGameSeat == Seat.NORTH) {
//                    windPoint.addPointScore(1);
//                }
//                if (gameSeat == Seat.NORTH) {
//                    windPoint.addPointScore(1);
//                }
//                windPoints.add(windPoint);
//            }
//        }
//        if (windPoints.size() == 4) {
//            points.add(Score.DAI_SAM_YUEN);
//        } else {
//            boolean pairIsWind = pairTile == Tile.WIND_EAST || pairTile == Tile.WIND_SOUTH
//                    || pairTile == Tile.WIND_WEST || pairTile == Tile.WIND_NORTH;
//            if (windPoints.size() == 3) {
//                if (pairIsWind) {
//                    points.add(Score.SIU_SAM_YUEN);
//                } else {
//                    points.add(Score.DAI_SAM_FUNG);
//                }
//            } else if (windPoints.size() == 2) {
//                if (pairIsWind) {
//                    points.add(Score.SIU_SAM_FUNG);
//                } else {
//                    points.addAll(windPoints);
//                }
//            } else {
//                points.addAll(windPoints);
//            }
//        }
//
//
//        // DRAGON TILES (中發白, 大小三元)
//        List<Score> dragonPoints = new ArrayList<>();
//        for (List<Tile> group : pongsAndKongs) {
//            if (group.getFirst() == Tile.WORD_ZHONG) {
//                dragonPoints.add(Score.RED_DRAGON);
//            } else if (group.getFirst() == Tile.WORD_FAT) {
//                dragonPoints.add(Score.GREEN_DRAGON);
//            } else if (group.getFirst() == Tile.WORD_BAT) {
//                dragonPoints.add(Score.WHITE_DRAGON);
//            }
//        }
//        if (dragonPoints.size() == 3) {
//            points.add(Score.DAI_SAM_FUNG);
//        } else if (dragonPoints.size() == 2) {
//            if (pairTile == Tile.WORD_ZHONG || pairTile == Tile.WORD_FAT
//                    || pairTile == Tile.WORD_BAT) {
//                points.add(Score.SIU_SAM_FUNG);
//            } else {
//                points.addAll(dragonPoints);
//            }
//        } else {
//            points.addAll(dragonPoints);
//        }
//
//
//        // ALL GROUPS ARE SHEUNGS (平糊)
//        if (sheungs.size() == 5) {
//            points.add(Score.PING_WU);
//        }
//
//
//        // FINAL PAIR IS 2, 5, OR 8 (將眼)
//        if ((pairTile.getTileType() == TileType.TUNG || pairTile.getTileType() == TileType.SOK
//                || pairTile.getTileType() == TileType.MAAN) && (pairTile.getTileNum() == 2
//                || pairTile.getTileNum() == 5 || pairTile.getTileNum() == 8)) {
//            points.add(Score.TSEUNG_AN);
//        }
//
//
//        // 123 + 789, or 111 + 999 OF A SAME TYPE (老少)
//        for (int i = 0; i < numSheungs; i++) {
//            for (int j = i + 1; j < numSheungs; j++) {
//                List<Tile> group1 = sheungs.get(i);
//                Tile tile1 = group1.getFirst();
//                List<Tile> group2 = sheungs.get(j);
//                Tile tile2 = group2.getFirst();
//                if (tile1.getTileType() == tile2.getTileType()
//                        && tile1.getTileNum() == 1 && tile2.getTileNum() == 7) {
//                    points.add(Score.LOU_SIU);
//                    break;
//                }
//            }
//        }
//        for (int i = 0; i < numPongsAndKongs; i++) {
//            for (int j = i + 1; j < numPongsAndKongs; j++) {
//                List<Tile> group1 = pongsAndKongs.get(i);
//                Tile tile1 = group1.getFirst();
//                List<Tile> group2 = pongsAndKongs.get(j);
//                Tile tile2 = group2.getFirst();
//                boolean notWord = true;
//                for (TileType tileType : wordTypes) {
//                    if (tile1.getTileType() == tileType) {
//                        notWord = false;
//                        break;
//                    }
//                }
//                if (notWord && tile1.getTileType() == tile2.getTileType()
//                        && tile1.getTileNum() == 1 && tile2.getTileNum() == 9) {
//                    points.add(Score.LOU_SIU);
//                    break;
//                }
//            }
//        }
//
//
//        // NO WORDS (無字)
//        if (winds.isEmpty() && dragons.isEmpty()) {
//            points.add(Score.MO_ZI);
//        }
//
//
//        // NO WORDS OR FLOWERS (無字花)
//        if (points.contains(Score.MO_FA) && points.contains(Score.MO_ZI)) {
//            points.remove(Score.MO_FA);
//            points.remove(Score.MO_ZI);
//            points.add(Score.MO_ZI_FA);
//        }
//
//
//        // NO WORDS OR FLOWERS, AND ALL GROUPS ARE SHEUNGS (大平糊)
//        if (points.contains(Score.MO_ZI_FA) && points.contains(Score.PING_WU)) {
//            points.remove(Score.MO_ZI_FA);
//            points.remove(Score.PING_WU);
//            points.add(Score.ALL_SHEUNGS_NO_WORDS_OR_FLOWERS);
//        }
//
//
//        // ALL GROUPS ARE UNREVEALED (門清, 門清自摸)
//        if (brightSheungs.isEmpty() && brightPongs.isEmpty()
//                && points.contains(Score.SELF_DRAW)) {
//            points.remove(Score.SELF_DRAW);
//            points.add(Score.ALL_CONCEALED_SELF_DRAW);
//        } else if (brightSheungs.isEmpty() && brightPongs.isEmpty()) {
//            points.add(Score.MUN_TSING);
//        }
//
//
//        // KONGS (摃)
//        for (List<Tile> group : brightKongs) {
//            points.add(Score.BRIGHT_KONG);
//        }
//        for (List<Tile> group : darkKongs) {
//            points.add(Score.DARK_KONG);
//        }
//
//
//        // COLLECTION OF TYPES (N門齊, 缺一門, 混一色, 清一色)
//        int typeCount = 0;
//        if (!tungs.isEmpty()) typeCount++;
//        if (!soks.isEmpty()) typeCount++;
//        if (!maans.isEmpty()) typeCount++;
//        if (typeCount == 3 && !winds.isEmpty() && !dragons.isEmpty()) {
//            if (numFlowerSeason > 0 && numFlowerPlant > 0) {
//                points.add(Score.TSAT_MUN_CHAI);
//            } else {
//                points.add(Score.MM_MUN_CHAI);
//            }
//        } else if (typeCount == 2 && winds.isEmpty() && dragons.isEmpty()) {
//            points.add(Score.TWO_SUITS);
//        } else if (typeCount == 1) {
//            if (winds.isEmpty() && dragons.isEmpty()) {
//                points.add(Score.TSING_YAT_SIK);
//            } else {
//                points.add(Score.WUN_YAT_SIK);
//            }
//        }
//
//
//        // ALL GROUPS ARE PONGS/KONGS (對對胡, 間間胡)
//        if (pongsAndKongs.size() == 5) {
//            if (points.contains(Score.ALL_CONCEALED_SELF_DRAW)) {
//                points.remove(Score.ALL_CONCEALED_SELF_DRAW);
//                points.add(Score.ALL_CONCEALED_PONGS_SELF_DRAW);
//            } else {
//                points.add(Score.ALL_PONGS);
//            }
//        }
//
//
//        // WIN OFF ONE POSSIBLE TILE (獨獨, 假獨)
//        if (wonOffTile == pairTile) {
//            for (List<Tile> group : darkSheungs) {
//                Tile starterTile = group.getFirst();
//                Tile enderTile = group.getLast();
//                if (((wonOffTile == starterTile && wonOffTile.getTileNum() != 7)
//                        || (wonOffTile == enderTile && wonOffTile.getTileNum() != 3))
//                        || (checkSameType(List.of(wonOffTile, starterTile))
//                        && (wonOffTile.getTileNum() == starterTile.getTileNum() - 1
//                        || wonOffTile.getTileNum() == starterTile.getTileNum() + 3))) {
//                    points.add(Score.SINGLE_WAIT_FOR_TWO_MELDS);
//                } else if (checkSameType(List.of(wonOffTile, starterTile))) {
//                    for (List<Tile> pongGroup : darkPongs) {
//                        Tile pongTile = pongGroup.getFirst();
//                        if ((checkSameType(List.of(wonOffTile, pongTile))
//                                && ((wonOffTile.getTileNum() == pongTile.getTileNum() - 1
//                                && wonOffTile.getTileNum() == starterTile.getTileNum() - 2)
//                                || (wonOffTile.getTileNum() == pongTile.getTileNum() + 1
//                                && wonOffTile.getTileNum() == starterTile.getTileNum() + 4)))) {
//                            points.add(Score.SINGLE_WAIT_FOR_TWO_MELDS);
//                            break;
//                        }
//                    }
//                }
//                if (points.contains(Score.SINGLE_WAIT_FOR_TWO_MELDS)) {
//                    break;
//                }
//            }
//            if (!points.contains(Score.SINGLE_WAIT_FOR_TWO_MELDS)) {
//                points.add(Score.DUK_DUK);
//            }
//        } else {
//            for (List<Tile> group : darkSheungs) {
//                Tile starterTile = group.getFirst();
//                Tile middleTile = group.get(1);
//                Tile enderTile = group.getLast();
//                if (wonOffTile == middleTile) {
//                    points.add(Score.DUK_DUK);
//                    break;
//                } else if (wonOffTile == enderTile && wonOffTile.getTileNum() == 3) {
//                    for (List<Tile> otherGroup : darkSheungs) {
//                        Tile otherStarterTile = otherGroup.getFirst();
//                        if (wonOffTile == otherStarterTile) {
//                            points.add(Score.SINGLE_WAIT_FOR_TWO_MELDS);
//                            break;
//                        }
//                    }
//                    if (!points.contains(Score.SINGLE_WAIT_FOR_TWO_MELDS)) {
//                        points.add(Score.DUK_DUK);
//                        break;
//                    }
//                } else if (wonOffTile == starterTile && wonOffTile.getTileNum() == 7) {
//                    for (List<Tile> otherGroup : darkSheungs) {
//                        Tile otherEnderTile = otherGroup.getLast();
//                        if (wonOffTile == otherEnderTile) {
//                            points.add(Score.SINGLE_WAIT_FOR_TWO_MELDS);
//                            break;
//                        }
//                    }
//                    if (!points.contains(Score.SINGLE_WAIT_FOR_TWO_MELDS)) {
//                        points.add(Score.DUK_DUK);
//                        break;
//                    }
//                }
//            }
//        }
//
//
//        // WIN OFF TWO POSSIBLE PONGS (對碰)
//        for (List<Tile> group : darkPongs) {
//            if (group.contains(wonOffTile)) {
//                points.add(Score.DUI_PONG);
//                break;
//            }
//        }
//
//
//        // CONTAINS 1, 9, AND WORD TILES (斷么, 全帶么, 混么, 清么)
//        int numOneNineTiles = 0;
//        int numGroupsWithOneNineTiles = 0;
//        int numWordTiles = 0;
//        int numGroupsWithWordTiles = 0;
//        int numNonOneNineWordTiles = 0;
//        for (List<Tile> group : allGroups) {
//            boolean hasOneNineTile = false;
//            boolean hasWordTile = false;
//            for (Tile tile : group) {
//                if (tile.getTileType() == TileType.WORD_WIND
//                        || tile.getTileType() == TileType.WORD_DRAGON) {
//                    numWordTiles += 1;
//                    hasWordTile = true;
//                } else if (tile.getTileNum() == 1 || tile.getTileNum() == 9) {
//                    numOneNineTiles += 1;
//                    hasOneNineTile = true;
//                } else {
//                    numNonOneNineWordTiles += 1;
//                }
//            }
//            if (hasOneNineTile) {
//                numGroupsWithOneNineTiles += 1;
//            }
//            if (hasWordTile) {
//                numGroupsWithWordTiles += 1;
//            }
//        }
//        if (numOneNineTiles + numWordTiles == 0) {
//            points.add(Score.DUEN_YIU);
//        } else if (numWordTiles + numNonOneNineWordTiles == 0) {
//            points.add(Score.ALL_WONDERS_NO_WORDS);
//        } else if (numNonOneNineWordTiles == 0) {
//            points.add(Score.ALL_WONDERS);
//        } else if (numGroupsWithOneNineTiles == allGroups.size()) {
//            points.add(Score.ALL_USING_WONDERS_NO_WORDS);
//        } else if (numGroupsWithOneNineTiles + numGroupsWithWordTiles == allGroups.size()) {
//            points.add(Score.ALL_USING_WONDERS);
//        }
//
//
//        // FOUR IDENTICAL TILES ACROSS DIFFERENT GROUPS (四歸一,四歸二,四歸四)
//        Map<Tile, Integer> numOccurrences = new HashMap<>();
//        Map<Tile, Integer> numDifferentGroups = new HashMap<>();
//        for (List<Tile> group : sheungs) {
//            for (Tile tile : group) {
//                numOccurrences.put(tile, numOccurrences.getOrDefault(tile, 0) + 1);
//                numDifferentGroups.put(tile, numDifferentGroups.getOrDefault(tile, 0) + 1);
//            }
//        }
//        for (List<Tile> group : pongsAndKongs) {
//            Tile starterTile = group.getFirst();
//            numDifferentGroups.put(starterTile,
//                    numDifferentGroups.getOrDefault(starterTile, 0) + 1);
//            for (Tile tile : group) {
//                numOccurrences.put(tile, numOccurrences.getOrDefault(tile, 0) + 1);
//            }
//        }
//        numOccurrences.put(pairTile, numOccurrences.getOrDefault(pairTile, 0) + 1);
//        numOccurrences.put(pairTile, numOccurrences.getOrDefault(pairTile, 0) + 1);
//        numDifferentGroups.put(pairTile,
//                numDifferentGroups.getOrDefault(pairTile, 0) + 1);
//        for (Map.Entry<Tile, Integer> entry : numOccurrences.entrySet()) {
//            Tile tile = entry.getKey();
//            int count = entry.getValue();
//            if (count == 4) {
//                int countGroups = numDifferentGroups.get(tile);
//                switch (countGroups) {
//                    case 4 -> points.add(Score.FOUR_TILES_FOUR_GROUPS);
//                    case 3 -> points.add(Score.FOUR_TILES_THREE_GROUPS);
//                    case 2 -> points.add(Score.FOUR_TILES_TWO_GROUPS);
//                }
//            }
//        }
//
//
//        // KONGS AND DARK PONGS (暗刻)
//        if (!points.contains(Score.ALL_CONCEALED_PONGS_SELF_DRAW)) {
//            int numDarkHaks = kongs.size();
//            for (List<Tile> group : darkPongs) {
//                if (group.getFirst() != wonOffTile || points.contains(Score.SELF_DRAW)) {
//                    numDarkHaks += 1;
//                }
//            }
//            switch (numDarkHaks) {
//                case 5 -> points.add(Score.MM_UM_HAK);
//                case 4 -> points.add(Score.FOUR_CONCEALED_PONGS);
//                case 3 -> points.add(Score.SAM_UM_HAK);
//                case 2 -> points.add(Score.TWO_CONCEALED_PONGS);
//            }
//        }
//
//
//        // INITIALIZERS FOR SHEUNG-CHECKING
//        Map<Tile, Integer> sheungCount = new HashMap<>();
//        Map<Integer, Integer> fungCount = new HashMap<>();
//        for (List<Tile> group : sheungs) {
//            Tile starterTile = group.getFirst();
//            int tileNum = starterTile.getTileNum();
//            sheungCount.put(starterTile, sheungCount.getOrDefault(starterTile, 0) + 1);
//            fungCount.put(tileNum, fungCount.getOrDefault(tileNum, 0) + 1);
//        }
//
//        // 4+ SHEUNGS WITH SAME NUMBER (同順)
//        for (Map.Entry<Integer, Integer> entry : fungCount.entrySet()) {
//            switch (entry.getValue()) {
//                case 5 -> points.add(Score.FIVE_ENCOUNTERS);
//                case 4 -> points.add(Score.FOUR_ENCOUNTERS);
//            }
//        }
//
//        // only check for 般高/相逢 when there is no 同順
//        if (!points.contains(Score.FIVE_ENCOUNTERS)
//                && !points.contains(Score.FOUR_ENCOUNTERS)) {
//
//            // IDENTICAL SHEUNGS (般高)
//            for (Map.Entry<Tile, Integer> entry : sheungCount.entrySet()) {
//                switch (entry.getValue()) {
//                    case 4 -> points.add(Score.SEI_PUN_KO);
//                    case 3 -> points.add(Score.THREE_IDENTICAL_SHEUNGS);
//                    case 2 -> points.add(Score.YAT_PUN_KO);
//                }
//            }
//
//            // SHEUNGS WITH SAME NUMBERS BUT DIFFERENT TYPES (相逢)
//            List<List<Tile>> unusedSheungs = new ArrayList<>(sheungs);
//            for (int i = 0; i < numSheungs; i++) {
//                List<Tile> group1 = sheungs.get(i);
//                if (unusedSheungs.contains(group1)) {
//                    Tile tile1 = group1.getFirst();
//                    for (int j = i + 1; j < numSheungs; j++) {
//                        List<Tile> group2 = sheungs.get(j);
//                        if (unusedSheungs.contains(group2)) {
//                            Tile tile2 = group2.getFirst();
//                            boolean samSheungFungFlag = false;
//                            for (int k = j + 1; k < numSheungs; k++) {
//                                List<Tile> group3 = sheungs.get(k);
//                                if (unusedSheungs.contains(group3)) {
//                                    Tile tile3 = group3.getFirst();
//                                    List<Tile> checkSamSheungFung = List.of(tile1, tile2, tile3);
//                                    if (checkDifferentType(checkSamSheungFung)
//                                            && checkSameNum(checkSamSheungFung)) {
//                                        points.add(Score.SAM_SHEUNG_FUNG);
//                                        unusedSheungs.remove(group1);
//                                        unusedSheungs.remove(group2);
//                                        unusedSheungs.remove(group3);
//                                        samSheungFungFlag = true;
//                                    }
//                                }
//                            }
//                            if (!samSheungFungFlag) {
//                                List<Tile> checkYeeSheungFung = List.of(tile1, tile2);
//                                if (checkDifferentType(checkYeeSheungFung)
//                                        && checkSameNum(checkYeeSheungFung)) {
//                                    points.add(Score.TWO_SHEUNGS_MATCHING_SEQUENCE);
//                                    unusedSheungs.remove(group1);
//                                    unusedSheungs.remove(group2);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // PONGS WITH SAME NUMBER BUT DIFFERENT TYPES (兄弟)
//        Map<Integer, Integer> hingDaiCount = new HashMap<>();
//        for (List<Tile> group : pongsAndKongs) {
//            Tile starterTile = group.getFirst();
//            if (nonWordTypes.contains(starterTile.getTileType())) {
//                int tileNum = starterTile.getTileNum();
//                hingDaiCount.put(tileNum, hingDaiCount.getOrDefault(tileNum, 0) + 1);
//            }
//        }
//        for (Map.Entry<Integer, Integer> entry : hingDaiCount.entrySet()) {
//            int tileNum = entry.getKey();
//            switch (entry.getValue()) {
//                case 3 -> points.add(Score.DAI_SAM_HING_DAI);
//                case 2 -> {
//                    if (tileNum == pairTile.getTileNum()) {
//                        points.add(Score.LITTLE_THREE_BROTHERS);
//                    } else {
//                        points.add(Score.TWO_PONGS_MATCHING_NUMBER);
//                    }
//                }
//            }
//        }
//
//
//        // PONGS WITH ASCENDING ORDER (姊妹)
//        for (int i = 0; i < numPongsAndKongs; i++) {
//            List<Tile> group1 = pongsAndKongs.get(i);
//            Tile tile1 = group1.getFirst();
//            for (int j = i + 1; j < numPongsAndKongs; j++) {
//                List<Tile> group2 = pongsAndKongs.get(j);
//                Tile tile2 = group2.getFirst();
//                if (checkSameType(List.of(tile1, tile2))
//                        && tile1.getTileNum() == tile2.getTileNum() - 1) {
//                    boolean daiSamFlag = false;
//                    for (int k = j + 1; k < numPongsAndKongs; k++) {
//                        List<Tile> group3 = pongsAndKongs.get(k);
//                        Tile tile3 = group3.getFirst();
//                        if (checkSameType(List.of(tile1, tile3))) {
//                            if (tile2.getTileNum() == tile3.getTileNum() - 1) {
//                                points.add(Score.BIG_THREE_SISTERS);
//                                daiSamFlag = true;
//                            }
//                        }
//                    }
//                    if (!daiSamFlag && checkSameType(List.of(tile1, pairTile))
//                            && (pairTile.getTileNum() == tile1.getTileNum() - 1
//                            || pairTile.getTileNum() == tile2.getTileNum() + 1)) {
//                        points.add(Score.LITTLE_THREE_SISTERS);
//                    }
//                }
//            }
//        }
//
//
//        // DARK SHEUNGS OF 123, 456, 789 OF ONE TYPE (暗龍)
//        List<List<Tile>> unusedSheungs = new ArrayList<>(sheungs);
//        List<TileType> umLungTypes = new ArrayList<>();
//        for (int i = 0; i < numDarkSheungs; i++) {
//            List<Tile> group1 = darkSheungs.get(i);
//            Tile tile1 = group1.getFirst();
//            for (int j = i + 1; j < numDarkSheungs; j++) {
//                List<Tile> group2 = darkSheungs.get(j);
//                Tile tile2 = group2.getFirst();
//                if (checkSameType(List.of(tile1, tile2))
//                        && tile1.getTileNum() == tile2.getTileNum() - 3) {
//                    for (int k = j + 1; k < numDarkSheungs; k++) {
//                        List<Tile> group3 = darkSheungs.get(k);
//                        Tile tile3 = group3.getFirst();
//                        if (checkSameType(List.of(tile1, tile2, tile3))
//                                && tile2.getTileNum() == tile3.getTileNum() - 3
//                                && unusedSheungs.contains(group1)
//                                && unusedSheungs.contains(group2)
//                                && unusedSheungs.contains(group3)) {
//                            points.add(Score.UM_LUNG);
//                            umLungTypes.add(tile1.getTileType());
//                            unusedSheungs.remove(group1);
//                            unusedSheungs.remove(group2);
//                            unusedSheungs.remove(group3);
//                            if (unusedSheungs.contains(group1) && unusedSheungs.contains(group2)) {
//                                unusedSheungs.remove(group1);
//                                unusedSheungs.remove(group2);
//                                points.add(Score.UM_LUNG);
//                            } else if (unusedSheungs.contains(group1)
//                                    && unusedSheungs.contains(group3)) {
//                                unusedSheungs.remove(group1);
//                                unusedSheungs.remove(group3);
//                                points.add(Score.UM_LUNG);
//                            } else if (unusedSheungs.contains(group2)
//                                    && unusedSheungs.contains(group3)) {
//                                unusedSheungs.remove(group2);
//                                unusedSheungs.remove(group3);
//                                points.add(Score.UM_LUNG);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // SHEUNGS OF 123, 456, 789 OF ONE TYPE (明龍)
//        for (TileType type : nonWordTypes) {
//            if (umLungTypes.contains(type)) {
//                List<Integer> nums = new ArrayList<>(List.of(1, 4, 7));
//                for (List<Tile> group : brightSheungs) {
//                    Tile tile1 = group.getFirst();
//                    if (tile1.getTileType() == type && nums.contains(tile1.getTileNum())) {
//                        nums.remove(Integer.valueOf(tile1.getTileNum()));
//                    }
//                }
//                if (nums.size() == 1) {
//                    points.add(Score.MING_LUNG);
//                }
//            } else {
//                unusedSheungs = new ArrayList<>(sheungs);
//                for (int i = 0; i < numSheungs; i++) {
//                    List<Tile> group1 = sheungs.get(i);
//                    Tile tile1 = group1.getFirst();
//                    if (tile1.getTileType() == type) {
//                        for (int j = i + 1; j < numSheungs; j++) {
//                            List<Tile> group2 = sheungs.get(j);
//                            Tile tile2 = group2.getFirst();
//                            if (checkSameType(List.of(tile1, tile2))
//                                    && tile1.getTileNum() == tile2.getTileNum() - 3) {
//                                for (int k = j + 1; k < numSheungs; k++) {
//                                    List<Tile> group3 = sheungs.get(k);
//                                    Tile tile3 = group3.getFirst();
//                                    if (checkSameType(List.of(tile1, tile3))
//                                            && tile2.getTileNum()
//                                            == tile3.getTileNum() - 3) {
//                                        points.add(Score.MING_LUNG);
//                                        unusedSheungs.remove(group1);
//                                        unusedSheungs.remove(group2);
//                                        unusedSheungs.remove(group3);
//                                        if (unusedSheungs.contains(group1)
//                                                && unusedSheungs.contains(group2)) {
//                                            unusedSheungs.remove(group1);
//                                            unusedSheungs.remove(group2);
//                                            points.add(Score.MING_LUNG);
//                                        } else if (unusedSheungs.contains(group1)
//                                                && unusedSheungs.contains(group3)) {
//                                            unusedSheungs.remove(group1);
//                                            unusedSheungs.remove(group3);
//                                            points.add(Score.MING_LUNG);
//                                        } else if (unusedSheungs.contains(group2)
//                                                && unusedSheungs.contains(group3)) {
//                                            unusedSheungs.remove(group2);
//                                            unusedSheungs.remove(group3);
//                                            points.add(Score.MING_LUNG);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // DARK SHEUNGS OF 123, 456, 789 OF THREE DIFFERENT TYPES (暗雜龍)
//        List<List<Tile>> umZhapLungStarters = new ArrayList<>();
//        List<List<Tile>> zhapLungDarkSheungs = new ArrayList<>(darkSheungs);
//        HandSorter.sortSheungsByNum(zhapLungDarkSheungs);
//        List<List<Integer>> umZhapLungCombinations = new ArrayList<>();
//        for (int i = 0; i < numDarkSheungs; i++) {
//            List<Tile> group1 = zhapLungDarkSheungs.get(i);
//            Tile tile1 = group1.getFirst();
//            for (int j = i + 1; j < numDarkSheungs; j++) {
//                List<Tile> group2 = zhapLungDarkSheungs.get(j);
//                Tile tile2 = group2.getFirst();
//                if (checkDifferentType(List.of(tile1, tile2))
//                        && tile1.getTileNum() == tile2.getTileNum() - 3) {
//                    for (int k = j + 1; k < numDarkSheungs; k++) {
//                        List<Tile> group3 = zhapLungDarkSheungs.get(k);
//                        Tile tile3 = group3.getFirst();
//                        if (checkDifferentType(List.of(tile1, tile2, tile3))
//                                && tile2.getTileNum() == tile3.getTileNum() - 3) {
//                            boolean twoGroupsAlreadyUsed = false;
//                            for (List<Integer> combination : umZhapLungCombinations) {
//                                if ((combination.contains(i) && combination.contains(j))
//                                        || (combination.contains(i) && combination.contains(k))
//                                        || (combination.contains(j) && combination.contains(k))) {
//                                    twoGroupsAlreadyUsed = true;
//                                    break;
//                                }
//                            }
//                            if (!twoGroupsAlreadyUsed) {
//                                umZhapLungCombinations.add(List.of(i, j, k));
//                                umZhapLungStarters.add(List.of(tile1, tile2, tile3));
//                                points.add(Score.CONCEALED_MIXED_DRAGON);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // SHEUNGS OF 123, 456, 789 OF THREE DIFFERENT TYPES (明雜龍)
//        List<List<Tile>> zhapLungSheungs = new ArrayList<>(sheungs);
//        HandSorter.sortSheungsByNum(zhapLungSheungs);
//        List<List<Integer>> zhapLungCombinations = new ArrayList<>();
//        unusedSheungs = new ArrayList<>(sheungs);
//        for (int i = 0; i < numSheungs; i++) {
//            List<Tile> group1 = zhapLungSheungs.get(i);
//            Tile tile1 = group1.getFirst();
//            for (int j = i + 1; j < numSheungs; j++) {
//                List<Tile> group2 = zhapLungSheungs.get(j);
//                Tile tile2 = group2.getFirst();
//                if (checkDifferentType(List.of(tile1, tile2))
//                        && tile1.getTileNum() == tile2.getTileNum() - 3) {
//                    for (int k = j + 1; k < numSheungs; k++) {
//                        List<Tile> group3 = zhapLungSheungs.get(k);
//                        Tile tile3 = group3.getFirst();
//                        if (checkDifferentType(List.of(tile1, tile2, tile3))
//                                && tile2.getTileNum() == tile3.getTileNum() - 3) {
//                            boolean twoGroupsAlreadyUsed = false;
//                            for (List<Integer> combination : zhapLungCombinations) {
//                                if ((combination.contains(i) && combination.contains(j))
//                                        || (combination.contains(i) && combination.contains(k))
//                                        || (combination.contains(j) && combination.contains(k))) {
//                                    twoGroupsAlreadyUsed = true;
//                                    break;
//                                }
//                            }
//                            if (!twoGroupsAlreadyUsed) {
//                                List<Tile> starters = List.of(tile1, tile2, tile3);
//                                if (umZhapLungStarters.contains(starters)) {
//                                    unusedSheungs.remove(group1);
//                                    unusedSheungs.remove(group2);
//                                    unusedSheungs.remove(group3);
//                                    if (unusedSheungs.contains(group1)
//                                            && unusedSheungs.contains(group2)) {
//                                        points.add(Score.MELDED_MIXED_DRAGON);
//                                    } else if (unusedSheungs.contains(group1)
//                                            && unusedSheungs.contains(group3)) {
//                                        points.add(Score.MELDED_MIXED_DRAGON);
//                                    } else if (unusedSheungs.contains(group2)
//                                            && unusedSheungs.contains(group3)) {
//                                        points.add(Score.MELDED_MIXED_DRAGON);
//                                    }
//                                } else {
//                                    zhapLungCombinations.add(List.of(i, j, k));
//                                    points.add(Score.MELDED_MIXED_DRAGON);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//
//        // ALL TILES ARE BRIGHT (全求人, 半求人)
//        if (brights.size() == 5) {
//            if (points.contains(Score.SELF_DRAW)) {
//                points.add(Score.BOON_KAU_YAN);
//            } else {
//                points.add(Score.ALL_MELDED);
//            }
//        }
//
//
//        // WIN AFTER BONUS DRAWS (花上自摸, 摃上自摸)
//        if (lastEvent.equals("flower") && points.contains(Score.SELF_DRAW)) {
//            points.add(Score.SELF_DRAW_AFTER_DRAWING_FLOWER);
//        } else if (lastEvent.equals("double kong") && points.contains(Score.SELF_DRAW)) {
//            points.add(Score.SELF_DRAW_AFTER_TWO_KONGS);
//        } else if (lastEvent.equals("kong") && points.contains(Score.SELF_DRAW)) {
//            points.add(Score.SELF_DRAW_AFTER_KONG);
//        }
//
//
//        // WIN ON LAST FEW DRAWS (七只內, 十只內, 海底撈月)
//        if (numUnrevealedTiles == 8 && points.contains(Score.SELF_DRAW)) {
//            points.add(Score.WIN_ON_LAST_DRAW);
//        } else if (numUnrevealedTiles <= 11) {
//            points.add(Score.FIVE_DISCARDS_LEFT);
//        } else if (numUnrevealedTiles <= 14) {
//            points.add(Score.TEN_REMAINING_TILES);
//        }
//
//
//        // WIN ON FIRST FEW DRAWS (天胡, 地胡, 人胡)
//        if (playerGameSeat == roundSeat && discardCount == 0 && kongs.isEmpty()) {
//            points.add(Score.TIN_WU);
//        } else if (discardCount == 1 && !points.contains(Score.SELF_DRAW)) {
//            points.add(Score.DEI_WU);
//        } else if (discardCount <= 4) {
//            points.add(Score.YAN_WU);
//        }
//
//
//        // CHICKEN HAND (雞胡)
//        int sum = 0;
//        for (Score point : points) {
//            sum += point.getPointScore();
//        }
//        if (sum == 1) {
//            points.add(Score.CHICKEN_HAND);
//        }
//
//
//        // ZHONG (莊)
//        if (playerSeat == roundSeat || loserSeat == roundSeat) {
//            Score point = Score.ZHONG;
//            point.setPointScore(2 * lumZhongNum + 1);
//            points.add(point);
//        }
//
//        return points;
//    }
//
//    public static List<Score> getPointsThirteenOrphans(Seat gameSeat, Seat roundSeat, Seat playerSeat,
//                                                              Seat loserSeat, List<List<Tile>> hand,
//                                                              Tile wonOffTile, HandManager handManager,
//                                                              int numUnrevealedTiles, List<Tile> discardPile,
//                                                              boolean multipleWinners, int lumZhongNum,
//                                                              String lastEvent, int discardCount) {
//        List<Score> points = new ArrayList<>();
//        points.add(Score.SAP_SAM_YIU);
//
//
//
//
//        return new ArrayList<>();
//    }
//
//    public static List<Score> getPointsSixteenDisjoint(Seat gameSeat, Seat roundSeat, Seat playerSeat,
//                                                              Seat loserSeat, List<List<Tile>> hand,
//                                                              Tile wonOffTile, HandManager handManager,
//                                                              int numUnrevealedTiles, List<Tile> discardPile,
//                                                              boolean multipleWinners, int lumZhongNum,
//                                                              String lastEvent, int discardCount) {
//        return new ArrayList<>();
//    }
//
//    public static List<Score> getPointsLikKuLikKu(Seat gameSeat, Seat roundSeat, Seat playerSeat,
//                                                         Seat loserSeat, List<List<Tile>> hand,
//                                                         Tile wonOffTile, HandManager handManager,
//                                                         int numUnrevealedTiles, List<Tile> discardPile,
//                                                         boolean multipleWinners, int lumZhongNum,
//                                                         String lastEvent, int discardCount) {
//        return new ArrayList<>();
//    }
//
//    public static int getScore(List<Score> points) {
//        int score = 5;
//        for (Score point : points) {
//            score += point.getPointScore();
//        }
//        return score * 2;
//    }
//
//    /**
//     * Checks if the given tiles are of the same type.
//     * @param tiles the tiles to check.
//     * @return true iff the given tiles are of the same type, false otherwise.
//     */
//    public static boolean checkSameType(List<Tile> tiles) {
//        TileType type = tiles.getFirst().getTileType();
//        for (Tile tile : tiles) {
//            if (tile.getTileType() != type) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Checks if all given tiles are of different types.
//     * @param tiles the tiles to check.
//     * @return true iff all given tiles are of different types, false otherwise.
//     */
//    public static boolean checkDifferentType(List<Tile> tiles) {
//        if (tiles.size() > 3) {
//            return false;
//        }
//        List<TileType> existingTypes = new ArrayList<>();
//        for (Tile tile : tiles) {
//            TileType type = tile.getTileType();
//            if (existingTypes.contains(type)) {
//                return false;
//            }
//            existingTypes.add(type);
//        }
//        return true;
//    }
//
//    /**
//     * Checks if the given tiles have the same tile number.
//     * @param tiles the tiles to check.
//     * @return true iff the given tiles have the same tile number, false otherwise.
//     */
//    public static boolean checkSameNum(List<Tile> tiles) {
//        int tileNum = tiles.getFirst().getTileNum();
//        List<TileType> validTypes = List.of(TileType.TUNG, TileType.SOK, TileType.MAAN);
//        for (Tile tile : tiles) {
//            if (!validTypes.contains(tile.getTileType()) || tile.getTileNum() != tileNum) {
//                return false;
//            }
//        }
//        return true;
//    }
}
