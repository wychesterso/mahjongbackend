package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class ChickenHandMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        if (ctx.getScore() <= 1) {
            ctx.addScoringPattern(ScoringPattern.CHICKEN_HAND);
        }
    }
}
