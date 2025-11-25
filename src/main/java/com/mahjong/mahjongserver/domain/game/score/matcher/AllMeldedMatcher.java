package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class AllMeldedMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        if (ctx.numRevealedMelds() == 0) {
            if (ctx.containsScoringPattern(ScoringPattern.SELF_DRAW)) {
                ctx.addScoringPattern(ScoringPattern.ALL_MELDED_SELF_DRAW);
            } else {
                ctx.addScoringPattern(ScoringPattern.ALL_MELDED);
            }
        }
    }
}
