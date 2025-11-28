package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;

public class ZhongMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        Seat zhongSeat = ctx.getZhongSeat();
        if (zhongSeat == ctx.getWinnerSeat() || zhongSeat == ctx.getLoserSeat()) {
            ctx.addScoringPattern(ScoringPattern.DEALER);
        }
    }
}
