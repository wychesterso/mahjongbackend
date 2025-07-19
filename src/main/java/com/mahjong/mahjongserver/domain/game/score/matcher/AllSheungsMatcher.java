package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

public class AllSheungsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext scoringContext) {
        if (scoringContext.getAllSheungs().size() == 5) {
            scoringContext.addScoringPattern(ScoringPattern.ALL_SHEUNGS);
        }
    }
}
