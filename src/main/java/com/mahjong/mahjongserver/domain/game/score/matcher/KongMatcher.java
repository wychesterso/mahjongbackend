package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class KongMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        matchMeldedKong(ctx);
    }

    public void matchMeldedKong(ScoringContext scoringContext) {
        for (int i = 0; i < scoringContext.numBrightKongs(); i++) {
            scoringContext.addScoringPattern(ScoringPattern.MELDED_KONG);
        }
    }

    public void matchConcealedKong(ScoringContext scoringContext) {
        for (int i = 0; i < scoringContext.numDarkKongs(); i++) {
            scoringContext.addScoringPattern(ScoringPattern.CONCEALED_KONG);
        }
    }
}
