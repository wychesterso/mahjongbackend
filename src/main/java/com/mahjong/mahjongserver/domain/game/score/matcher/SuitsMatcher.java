package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class SuitsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        boolean hasWordTiles = ctx.getGroupedHand().hasWordTiles();

        int numRegularTypes = ctx.getGroupedHand().numNumberTileTypes();
        int numWordTypes = ctx.getGroupedHand().numWordTileTypes();
        int numFlowerTypes = ctx.getGroupedHand().numFlowerTileTypes();

        matchNoWords(ctx, hasWordTiles);
        matchTwoSuits(ctx, numRegularTypes, hasWordTiles);
        matchFiveOrSevenSuits(ctx, numRegularTypes, numWordTypes, numFlowerTypes);
        matchFlush(ctx, numRegularTypes, numWordTypes);
    }

    public void matchNoWords(ScoringContext ctx, boolean hasWords) {
        if (!hasWords) {
            if (ctx.ifExistsThenRemoveScoringPattern(ScoringPattern.NO_FLOWERS)) {
                ctx.addScoringPattern(ScoringPattern.NO_WORDS_OR_FLOWERS);
            } else {
                ctx.addScoringPattern(ScoringPattern.NO_WORDS);
            }
        }
    }

    public void matchTwoSuits(ScoringContext ctx, int numRegularTypes, boolean hasWords) {
        if (!hasWords && numRegularTypes == 2) {
            ctx.addScoringPattern(ScoringPattern.TWO_SUITS);
        }
    }

    public void matchFiveOrSevenSuits(ScoringContext ctx, int numRegularTypes, int numWordTypes, int numFlowerTypes) {
        if (numRegularTypes == 3 && numWordTypes == 2) {
            if (numFlowerTypes == 2) {
                ctx.addScoringPattern(ScoringPattern.SEVEN_SUITS);
            } else {
                ctx.addScoringPattern(ScoringPattern.FIVE_SUITS);
            }
        }
    }

    public void matchFlush(ScoringContext ctx, int numRegularTypes, int numWordTypes) {
        if (numRegularTypes == 1) {
            if (numWordTypes == 0) {
                ctx.addScoringPattern(ScoringPattern.FLUSH);
            } else {
                ctx.addScoringPattern(ScoringPattern.HALF_FLUSH);
            }
        }
    }
}
