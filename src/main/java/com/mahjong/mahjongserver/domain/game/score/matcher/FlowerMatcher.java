package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.Set;

public class FlowerMatcher implements ScoringPatternMatcher {

    private static final Set<Tile> SEASON_FLOWERS = Set.of(
            Tile.FLOWER_SPRING, Tile.FLOWER_SUMMER, Tile.FLOWER_AUTUMN, Tile.FLOWER_WINTER
    );

    private static final Set<Tile> PLANT_FLOWERS = Set.of(
            Tile.FLOWER_PLUM, Tile.FLOWER_ORCHID, Tile.FLOWER_CHRYSANTHEMUM, Tile.FLOWER_BAMBOO
    );

    @Override
    public void match(ScoringContext scoringContext) {
        Set<Tile> flowers = scoringContext.getFlowers();
        int numFlowers = flowers.size();

        if (numFlowers == 0) {
            scoringContext.addScoringPattern(ScoringPattern.NO_FLOWERS);
            return;
        }

        if (numFlowers == 8) {
            scoringContext.addScoringPattern(ScoringPattern.FLOWER_WIN);
            return;
        }

        boolean hasSeasonSet = flowers.containsAll(SEASON_FLOWERS);
        boolean hasPlantSet = flowers.containsAll(PLANT_FLOWERS);

        if (hasSeasonSet) {
            scoringContext.addScoringPattern(ScoringPattern.FLOWER_SET);
        }
        if (hasPlantSet) {
            scoringContext.addScoringPattern(ScoringPattern.FLOWER_SET);
        }

        for (Tile flower : flowers) {
            if ((hasSeasonSet && SEASON_FLOWERS.contains(flower)) ||
                    (hasPlantSet && PLANT_FLOWERS.contains(flower))) {
                continue; // skip GOOD/BAD scoring for flowers in completed sets
            }

            if (flower.getTileNum() == scoringContext.getWinnerSeat().ordinal() + 1) {
                scoringContext.addScoringPattern(ScoringPattern.GOOD_FLOWER);
            } else {
                scoringContext.addScoringPattern(ScoringPattern.BAD_FLOWER);
            }
        }
    }
}