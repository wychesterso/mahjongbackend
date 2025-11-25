package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class DragonTileMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<ScoringPattern> dragonPoints = new ArrayList<>();
        for (List<Tile> group : ctx.getAllPongsAndKongs()) {
            if (group.getFirst() == Tile.RED_DRAGON) {
                dragonPoints.add(ScoringPattern.RED_DRAGON);
            } else if (group.getFirst() == Tile.GREEN_DRAGON) {
                dragonPoints.add(ScoringPattern.GREEN_DRAGON);
            } else if (group.getFirst() == Tile.WHITE_DRAGON) {
                dragonPoints.add(ScoringPattern.WHITE_DRAGON);
            }
        }

        int dragonPongCount = dragonPoints.size();
        boolean pairIsDragon = ctx.getPair() != null &&
                ctx.getPairTile() != null &&
                isDragon(ctx.getPairTile());

        if (dragonPongCount == 3) {
            ctx.addScoringPattern(ScoringPattern.BIG_THREE_DRAGONS);
        } else if (dragonPongCount == 2) {
            if (pairIsDragon) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_DRAGONS);
            } else {
                ctx.addScoringPatterns(dragonPoints);
            }
        } else {
            ctx.addScoringPatterns(dragonPoints);
        }
    }

    private boolean isDragon(Tile tile) {
        return tile == Tile.RED_DRAGON || tile == Tile.GREEN_DRAGON || tile == Tile.WHITE_DRAGON;
    }
}
