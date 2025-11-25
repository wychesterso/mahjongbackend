package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

import java.util.List;

public class GeneralMatcher implements ScoringPatternMatcher {
    @Override
    public void match(ScoringContext ctx) {
        matchZhong(ctx);
        matchSelfDraw(ctx);
        matchMultipleWinners(ctx);
        matchGoodPair(ctx);
        matchWaitOnTwoPairs(ctx);
        matchAllConcealed(ctx);
    }

    private void matchZhong(ScoringContext ctx) {
        Seat zhongSeat = ctx.getZhongSeat();
        if (zhongSeat == ctx.getWinnerSeat() || zhongSeat == ctx.getLoserSeat()) {
            ctx.addScoringPattern(ScoringPattern.DEALER);
        }
    }

    private void matchSelfDraw(ScoringContext ctx) {
        if (ctx.isSelfDraw()) ctx.addScoringPattern(ScoringPattern.SELF_DRAW);
    }

    private void matchMultipleWinners(ScoringContext ctx) {
        if (ctx.getGame().getWinnerSeats().size() > 1) ctx.addScoringPattern(ScoringPattern.MULTIPLE_WINNERS);
    }

    private void matchGoodPair(ScoringContext ctx) {
        for (List<Tile> pair : ctx.getPairs()) {
            if (pair.getFirst().getTileType().getClassification() != TileClassification.REGULAR) continue;

            int tileNum = pair.getFirst().getTileNum();
            if (tileNum == 2 || tileNum == 5 || tileNum == 8) {
                ctx.addScoringPattern(ScoringPattern.GOOD_PAIR);
                return;
            }
        }
    }

    private void matchWaitOnTwoPairs(ScoringContext ctx) {
        if (ctx.getWinningGroupType() == MeldType.PONG) {
            ctx.addScoringPattern(ScoringPattern.WAIT_ON_TWO_PAIRS);
        }
    }

    private void matchAllConcealed(ScoringContext ctx) {
        if (ctx.numRevealedMelds() == 0) {
            if (ctx.ifExistsThenRemoveScoringPattern(ScoringPattern.SELF_DRAW)) {
                ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED_SELF_DRAW);
            } else {
                ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED);
            }
        }
    }
}
