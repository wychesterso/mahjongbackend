package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class WindMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext scoringContext) {
        List<ScoringPattern> windPoints = new ArrayList<>();
        Seat playerSeat = scoringContext.getWinnerSeat();
        Seat prevailingWind = scoringContext.getWindSeat();

        for (List<Tile> group : scoringContext.getAllPongsAndKongs()) {
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
        boolean pairIsWind = scoringContext.getPair() != null &&
                scoringContext.getPairTile() != null &&
                isWind(scoringContext.getPairTile());

        if (windPongCount == 4) {
            scoringContext.addScoringPattern(ScoringPattern.BIG_FOUR_WINDS); // 大四喜
        } else if (windPongCount == 3) {
            if (pairIsWind) {
                scoringContext.addScoringPattern(ScoringPattern.LITTLE_FOUR_WINDS); // 小四喜
            } else {
                scoringContext.addScoringPattern(ScoringPattern.BIG_THREE_WINDS); // 大三風
            }
        } else if (windPongCount == 2) {
            if (pairIsWind) {
                scoringContext.addScoringPattern(ScoringPattern.LITTLE_THREE_WINDS); // 小三風
            } else {
                scoringContext.addScoringPatterns(windPoints);
            }
        } else {
            scoringContext.addScoringPatterns(windPoints);
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
            case EAST -> ScoringPattern.EAST;
            case SOUTH -> ScoringPattern.SOUTH;
            case WEST -> ScoringPattern.WEST;
            case NORTH -> ScoringPattern.NORTH;
            default -> throw new IllegalArgumentException("Not a wind tile: " + windTile);
        };
    }
}