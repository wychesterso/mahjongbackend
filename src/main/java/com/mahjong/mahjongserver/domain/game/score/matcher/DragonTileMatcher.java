package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public class DragonTileMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<ScoringPattern> dragonPoints = new ArrayList<>();
        for (Meld meld : ctx.getGroupedHand().getPongsAndKongs()) {
            switch (meld.getStartingTile()) {
                case Tile.RED_DRAGON -> {dragonPoints.add(ScoringPattern.RED_DRAGON);}
                case Tile.GREEN_DRAGON -> {dragonPoints.add(ScoringPattern.GREEN_DRAGON);}
                case Tile.WHITE_DRAGON -> {dragonPoints.add(ScoringPattern.WHITE_DRAGON);}
            }
        }

        int dragonPongCount = dragonPoints.size();
        boolean hasDragonPair = ctx.getGroupedHand().getPairs().stream().anyMatch(
                a -> a.getStartingTile().getTileType() == TileType.DRAGON
        );

        if (dragonPongCount == 3) {
            ctx.addScoringPattern(ScoringPattern.BIG_THREE_DRAGONS);
        } else if (dragonPongCount == 2) {
            if (hasDragonPair) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_DRAGONS);
            } else {
                ctx.addScoringPatterns(dragonPoints);
            }
        } else {
            ctx.addScoringPatterns(dragonPoints);
        }
    }
}
