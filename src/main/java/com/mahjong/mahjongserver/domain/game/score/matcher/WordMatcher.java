package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class WordMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext scoringContext) {
        matchNoWords(scoringContext);
    }

    public void matchNoWords(ScoringContext scoringContext) {
        if (scoringContext.numWordGroups() == 0) {
            scoringContext.addScoringPattern(ScoringPattern.NO_WORDS);
        }
    }
}
