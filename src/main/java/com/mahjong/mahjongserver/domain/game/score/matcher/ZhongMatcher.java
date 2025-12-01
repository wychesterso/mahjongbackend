package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;

public class ZhongMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        Seat zhongSeat = ctx.getZhongSeat();
        int zhongScore = ctx.getLumZhongCount() * 2 + 1; // 2n+1

        if (zhongSeat == ctx.getWinnerSeat() || zhongSeat == ctx.getLoserSeat()) {
            ScoringPattern pattern = ScoringPattern.DEALER;
            pattern.setValue(zhongScore);
            ctx.addScoringPattern(pattern);
        }
    }
}
