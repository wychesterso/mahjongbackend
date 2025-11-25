package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class WindTileMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<ScoringPattern> windPoints = new ArrayList<>();
        Seat playerSeat = ctx.getWinnerSeat();
        Seat prevailingWind = ctx.getWindSeat();

        for (List<Tile> group : ctx.getAllPongsAndKongs()) {
            Tile first = group.getFirst();
            if (isWind(first)) {
                ScoringPattern windPattern = getWindPattern(first);
                int extra = 0;

                if (playerSeat != null && first == seatToTile(playerSeat)) {
                    extra++;
                }
                if (prevailingWind != null && first == seatToTile(prevailingWind)) {
                    extra++;
                }

                windPattern.addValue(extra);
                windPoints.add(windPattern);
            }
        }

        int windPongCount = windPoints.size();
        boolean pairIsWind = ctx.getPair() != null &&
                ctx.getPairTile() != null &&
                isWind(ctx.getPairTile());

        if (windPongCount == 4) {
            ctx.addScoringPattern(ScoringPattern.BIG_FOUR_WINDS); // 大四喜
        } else if (windPongCount == 3) {
            if (pairIsWind) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_FOUR_WINDS); // 小四喜
            } else {
                ctx.addScoringPattern(ScoringPattern.BIG_THREE_WINDS); // 大三風
            }
        } else if (windPongCount == 2) {
            if (pairIsWind) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_WINDS); // 小三風
            } else {
                ctx.addScoringPatterns(windPoints);
            }
        } else {
            ctx.addScoringPatterns(windPoints);
        }
    }

    private boolean isWind(Tile tile) {
        return tile == Tile.EAST || tile == Tile.SOUTH
                || tile == Tile.WEST || tile == Tile.NORTH;
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