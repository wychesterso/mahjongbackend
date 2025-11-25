package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;

public interface ScoringPatternMatcher {
    void match(ScoringContext ctx);
}