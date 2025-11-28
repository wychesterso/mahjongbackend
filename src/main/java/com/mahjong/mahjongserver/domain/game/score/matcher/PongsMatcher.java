package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.*;

public class PongsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        matchAllPongs(ctx);
        matchPongPatterns(ctx);
    }

    public void matchAllPongs(ScoringContext ctx) {
        if (ctx.getGroupedHand().numPongsAndKongs() == 5) {
            if (ctx.ifExistsThenRemoveScoringPattern(ScoringPattern.ALL_CONCEALED_SELF_DRAW)) {
                ctx.addScoringPattern(ScoringPattern.ALL_CONCEALED_PONGS_SELF_DRAW);
            } else {
                ctx.addScoringPattern(ScoringPattern.ALL_PONGS);
            }
        }
    }

    private void matchPongPatterns(ScoringContext ctx) {
        List<Meld> pongs = ctx.getGroupedHand().getPongsAndKongs();
        if (pongs.isEmpty()) return;

        Map<Integer, List<Meld>> byNumber = new HashMap<>();
        for (int i = 1; i <= 9; i++) byNumber.put(i, new ArrayList<>());

        for (Meld pong : pongs) {
            // group by num
            byNumber.get(pong.getStartingTile().getTileNum()).add(pong);
        }

        for (int i = 1; i <= 9; i++) {
            List<Meld> groupedPongs = byNumber.get(i);

            // find brothers
            matchBrothers(ctx, i, groupedPongs.size());
        }

        Map<TileType, List<Meld>> byType = new HashMap<>();
        for (TileType type : List.of(TileType.BAMBOO, TileType.CIRCLE, TileType.MILLION)) {
            byType.put(type, new ArrayList<>());
        }

        for (Meld pong : pongs) {
            // group by tile type
            TileType type = pong.getStartingTile().getTileType();
            if (type.getClassification() == TileClassification.REGULAR) {
                byType.get(type).add(pong);
            }
        }

        for (Map.Entry<TileType, List<Meld>> entry : byType.entrySet()) {
            // find sisters
            matchSisters(ctx, entry.getValue(), entry.getKey());
        }
    }

    //======================================================================
    // BROTHERS (same num)
    //======================================================================

    private void matchBrothers(ScoringContext ctx, int groupNum, int count) {
        if (matchBigThreeBrothers(ctx, count)) return;
        if (matchLittleThreeBrothers(ctx, groupNum, count)) return;
        matchTwoBrothers(ctx, count);
    }

    private boolean matchBigThreeBrothers(ScoringContext ctx, int count) {
        if (count == 3) {
            ctx.addScoringPattern(ScoringPattern.BIG_THREE_BROTHERS);
            return true;
        }
        return false;
    }

    private boolean matchLittleThreeBrothers(ScoringContext ctx, int groupNum, int count) {
        if (count == 2 && ctx.getGroupedHand().numPairs() == 1) {
            Tile tile = ctx.getGroupedHand().getPairs().getFirst().getStartingTile();
            if (tile.getTileNum() == groupNum) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_BROTHERS);
                return true;
            }
        }
        return false;
    }

    private boolean matchTwoBrothers(ScoringContext ctx, int count) {
        if (count == 2) {
            ctx.addScoringPattern(ScoringPattern.TWO_BROTHERS);
            return true;
        }
        return false;
    }

    //======================================================================
    // SISTERS (adjacent nums, same suit)
    //======================================================================

    private void matchSisters(ScoringContext ctx, List<Meld> groupedPongs, TileType type) {
        boolean[] found = new boolean[10];

        for (Meld pong : groupedPongs) found[pong.getStartingTile().getTileNum()] = true;

        for (int i = 1; i <= 7; i++) {
            if (matchBigThreeSisters(ctx, found, i) || matchLittleThreeSisters(ctx, found, i, type)) {
                i += 2;
            }
        }
    }

    private boolean matchBigThreeSisters(ScoringContext ctx, boolean[] found, int i) {
        if (found[i] && found[i + 1] && found[i + 2]) {
            ctx.addScoringPattern(ScoringPattern.BIG_THREE_SISTERS);
            return true;
        }
        return false;
    }

    private boolean matchLittleThreeSisters(ScoringContext ctx, boolean[] found, int i, TileType type) {
        if (found[i] && found[i + 1] && ctx.getGroupedHand().numPairs() == 1) {
            Tile tile = ctx.getGroupedHand().getPairs().getFirst().getStartingTile();
            if (tile.getTileType() == type && (tile.getTileNum() == i - 1 || tile.getTileNum() == i + 2)) {
                ctx.addScoringPattern(ScoringPattern.LITTLE_THREE_SISTERS);
                return true;
            }
        }
        return false;
    }
}
