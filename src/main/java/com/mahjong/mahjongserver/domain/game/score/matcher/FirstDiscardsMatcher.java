package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class FirstDiscardsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        int numDiscards = ctx.getGame().getNumDiscards();

        if (numDiscards == 0) {
            if (ctx.isSelfDraw()) {
                ctx.addScoringPattern(ScoringPattern.TIN_WU);
            } else {
                ctx.addScoringPattern(ScoringPattern.DEI_WU);
            }
        } else if (numDiscards <= 4) {
            ctx.addScoringPattern(ScoringPattern.YAN_WU);
        }
    }
}
