package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

public class GeneralMatcher implements ScoringPatternMatcher {
    private ScoringContext scoringContext;

    @Override
    public void match(ScoringContext scoringContext) {
        this.scoringContext = scoringContext;
        matchZhong();
        matchSelfDraw();
        matchMultipleWinners();
        matchGoodPair();
    }

    private void matchZhong() {
        Seat zhongSeat = scoringContext.getZhongSeat();
        if (zhongSeat == scoringContext.getWinnerSeat() || zhongSeat == scoringContext.getLoserSeat()) {
            scoringContext.addScoringPattern(ScoringPattern.ZHONG);
        }
    }

    private void matchSelfDraw() {
        if (scoringContext.isSelfDraw()) scoringContext.addScoringPattern(ScoringPattern.SELF_DRAW);
    }

    private void matchMultipleWinners() {
        if (scoringContext.getGame().getWinnerSeats().size() > 1) scoringContext.addScoringPattern(ScoringPattern.MULTIPLE_WINNERS);
    }

    private void matchGoodPair() {
        for (Tile tile : scoringContext.getConcealedPairs().getFirst()) {
            int tileNum = tile.getTileNum();
            if (tileNum == 2 || tileNum == 5 || tileNum == 8) {
                scoringContext.addScoringPattern(ScoringPattern.GOOD_PAIR);
                return;
            }
        }
    }
}
