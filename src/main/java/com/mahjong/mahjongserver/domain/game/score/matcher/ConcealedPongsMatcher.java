package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class ConcealedPongsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        switch (ctx.getGroupedHand().numConcealedPongsAndKongs()) {
            case 5 -> {
                if (ctx.getScoringPatterns().contains(ScoringPattern.SELF_DRAW) && ctx.getGroupedHand().numRevealedKongs() == 0) {
                    ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED_PONGS_SELF_DRAW);
                } else {
                    ctx.addScoringPattern(ScoringPattern.FIVE_CONCEALED_PONGS);
                }
            }
            case 4 -> { ctx.addScoringPattern(ScoringPattern.FOUR_CONCEALED_PONGS); }
            case 3 -> { ctx.addScoringPattern(ScoringPattern.THREE_CONCEALED_PONGS); }
            case 2 -> { ctx.addScoringPattern(ScoringPattern.TWO_CONCEALED_PONGS); }
        }
    }
}
