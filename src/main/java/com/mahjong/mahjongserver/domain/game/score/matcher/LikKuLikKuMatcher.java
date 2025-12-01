package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class LikKuLikKuMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        if (ctx.getGroupedHand().numRevealedPairs() >= 6) {
            ctx.addScoringPattern(ScoringPattern.LIK_KU_LIK_KU);
        }
    }
}
