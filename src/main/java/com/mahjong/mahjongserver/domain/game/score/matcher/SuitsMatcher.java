package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class SuitsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        boolean hasWords = ctx.hasWords();

        int numRegularTypes = (ctx.hasCircles() ? 1 : 0) + (ctx.hasBamboos() ? 1 : 0) + (ctx.hasMillions() ? 1 : 0);
        int numWordTypes = (ctx.hasWinds() ? 1 : 0) + (ctx.hasDragons() ? 1 : 0);
        int numFlowerTypes = (ctx.hasSeasonFlower() ? 1 : 0) + (ctx.hasPlantFlower() ? 1 : 0);

        matchNoWords(ctx, hasWords);
        matchTwoSuits(ctx, numRegularTypes, hasWords);
        matchFiveOrSevenSuits(ctx, numRegularTypes, numWordTypes, numFlowerTypes);
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
}
