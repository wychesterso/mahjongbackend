package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
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
        matchSelfDraw(ctx);
        matchMultipleWinners(ctx);
        matchGoodPair(ctx);
        matchWaitOnTwoPairs(ctx);
        matchAllConcealed(ctx);
    }

    private void matchSelfDraw(ScoringContext ctx) {
        if (ctx.isSelfDraw()) ctx.addScoringPattern(ScoringPattern.SELF_DRAW);
    }

    private void matchMultipleWinners(ScoringContext ctx) {
        if (ctx.getGame().getWinnerSeats().size() > 1) ctx.addScoringPattern(ScoringPattern.MULTIPLE_WINNERS);
    }

    private void matchGoodPair(ScoringContext ctx) {
        for (Meld pair : ctx.getGroupedHand().getPairs()) {
            Tile pairTile = pair.getStartingTile();
            if (pairTile.getTileType().getClassification() != TileClassification.REGULAR) continue;

            int tileNum = pairTile.getTileNum();
            if (tileNum == 2 || tileNum == 5 || tileNum == 8) {
                ctx.addScoringPattern(ScoringPattern.GOOD_PAIR);
                return;
            }
        }
    }

    private void matchWaitOnTwoPairs(ScoringContext ctx) {
        if (ctx.getWinningMeld().getMeldType() == MeldType.PONG) {
            ctx.addScoringPattern(ScoringPattern.WAIT_ON_TWO_PAIRS);
        }
    }

    private void matchAllConcealed(ScoringContext ctx) {
        int numRevealedMelds = ctx.getGroupedHand().numRevealedMelds();
        if (numRevealedMelds == 0) {
            ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED_SELF_DRAW);
        } else if (numRevealedMelds == 1 && ctx.getWinningMeld().isRevealed()) {
            ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED);
        }
    }
}
