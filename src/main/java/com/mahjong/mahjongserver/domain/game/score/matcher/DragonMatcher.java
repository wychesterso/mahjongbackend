package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

public class DragonMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        boolean dragonFound = false, mixedDragonFound = false;

        // FIRST INDEX - tile type, SECOND INDEX - starting num
        boolean[][] existsSheung = new boolean[3][3];

        // concealed dragons
        for (Meld sheung : ctx.getGroupedHand().getConcealedSheungs()) {
            Tile startingTile = sheung.getStartingTile();
            int startingNumIndex = startingNumToIndex(startingTile.getTileNum());
            if (startingNumIndex != -1) {
                existsSheung[tileTypeToIndex(startingTile.getTileType())][startingNumIndex] = true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (existsSheung[i][0]) {
                if (!dragonFound && existsSheung[i][1] && existsSheung[i][2]) {
                    ctx.addScoringPattern(ScoringPattern.CONCEALED_DRAGON);
                    dragonFound = true;
                } else if (!mixedDragonFound
                        && ((existsSheung[(i + 1) % 3][1] && existsSheung[(i + 2) % 3][2])
                        || (existsSheung[(i + 2) % 3][1] && existsSheung[(i + 1) % 3][2]))) {
                    ctx.addScoringPattern(ScoringPattern.CONCEALED_MIXED_DRAGON);
                    mixedDragonFound = true;
                }
            }
        }

        if (dragonFound || mixedDragonFound) return;

        // revealed dragons
        for (Meld sheung : ctx.getGroupedHand().getRevealedSheungs()) {
            Tile startingTile = sheung.getStartingTile();
            int startingNumIndex = startingNumToIndex(startingTile.getTileNum());
            if (startingNumIndex != -1) {
                existsSheung[tileTypeToIndex(startingTile.getTileType())][startingNumIndex] = true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (existsSheung[i][0]) {
                if (!dragonFound && existsSheung[i][1] && existsSheung[i][2]) {
                    ctx.addScoringPattern(ScoringPattern.MELDED_DRAGON);
                    dragonFound = true;
                } else if (!mixedDragonFound
                        && ((existsSheung[(i + 1) % 3][1] && existsSheung[(i + 2) % 3][2])
                        || (existsSheung[(i + 2) % 3][1] && existsSheung[(i + 1) % 3][2]))) {
                    ctx.addScoringPattern(ScoringPattern.MELDED_MIXED_DRAGON);
                    mixedDragonFound = true;
                }
            }
        }
    }

    private int tileTypeToIndex(TileType tileType) {
        switch (tileType) {
            case CIRCLE -> {return 0;}
            case BAMBOO -> {return 1;}
            case MILLION -> {return 2;}
            default -> {return -1;}
        }
    }

    private int startingNumToIndex(int startingNum) {
        switch (startingNum) {
            case 1 -> {return 0;}
            case 4 -> {return 1;}
            case 7 -> {return 2;}
            default -> {return -1;}
        }
    }
}
