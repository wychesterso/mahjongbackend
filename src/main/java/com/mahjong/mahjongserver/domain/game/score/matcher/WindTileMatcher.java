package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public class WindTileMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<ScoringPattern> windPoints = new ArrayList<>();
        Seat playerSeat = ctx.getWinnerSeat();
        Seat prevailingWind = ctx.getWindSeat();

        for (Meld meld : ctx.getGroupedHand().getPongsAndKongs()) {
            Tile tile = meld.getStartingTile();

            if (tile.getTileType() == TileType.WIND) {
                ScoringPattern windPattern = getWindPattern(tile);
                int extra = 0;

                if (playerSeat != null && tile == seatToTile(playerSeat)) {
                    extra++;
                }
                if (prevailingWind != null && tile == seatToTile(prevailingWind)) {
                    extra++;
                }

                windPattern.addValue(extra);
                windPoints.add(windPattern);
            }
        }

        int windPongCount = windPoints.size();
        boolean hasWindPair = ctx.getGroupedHand().getPairs().stream().anyMatch(
                a -> a.getStartingTile().getTileType() == TileType.WIND
        );

        if (windPongCount == 4) {
            ctx.addScoringPattern(ScoringPattern.BIG_FOUR_WINDS);
        } else if (windPongCount == 3) {
            if (hasWindPair) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_FOUR_WINDS);
            } else {
                ctx.addScoringPattern(ScoringPattern.BIG_THREE_WINDS);
            }
        } else if (windPongCount == 2) {
            if (hasWindPair) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_WINDS);
            } else {
                ctx.addScoringPatterns(windPoints);
            }
        } else {
            ctx.addScoringPatterns(windPoints);
        }
    }

    private Tile seatToTile(Seat seat) {
        return switch (seat) {
            case EAST -> Tile.EAST;
            case SOUTH -> Tile.SOUTH;
            case WEST -> Tile.WEST;
            case NORTH -> Tile.NORTH;
        };
    }

    private ScoringPattern getWindPattern(Tile windTile) {
        return switch (windTile) {
            case EAST -> ScoringPattern.EAST_WIND;
            case SOUTH -> ScoringPattern.SOUTH_WIND;
            case WEST -> ScoringPattern.WEST_WIND;
            case NORTH -> ScoringPattern.NORTH_WIND;
            default -> throw new IllegalArgumentException("Not a wind tile: " + windTile);
        };
    }
}