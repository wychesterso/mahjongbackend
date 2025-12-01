package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class ThirteenWondersMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        if (ctx.getGroupedHand().numThirteenOrphanGroups() == 1) {
            ctx.addScoringPattern(ScoringPattern.THIRTEEN_WONDERS);
        }
    }
}
