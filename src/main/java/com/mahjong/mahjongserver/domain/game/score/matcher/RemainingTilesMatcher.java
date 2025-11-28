package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class RemainingTilesMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        int numDrawsLeft = ctx.getGame().getBoard().getNumDrawsLeft();

        if (ctx.isSelfDraw() && numDrawsLeft == 0) {
            ctx.addScoringPattern(ScoringPattern.WIN_ON_LAST_DRAW);
        } else if (numDrawsLeft <= 5) {
            ctx.addScoringPattern(ScoringPattern.FIVE_REMAINING_TILES);
        } else if (numDrawsLeft <= 10) {
            ctx.addScoringPattern(ScoringPattern.TEN_REMAINING_TILES);
        }
    }
}
