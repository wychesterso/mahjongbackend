package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

import java.util.List;

public class WondersMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        int countRevealedFullOneOrNine = 0, countConcealedFullOneOrNine = 0;
        int countRevealedContainsOneOrNine = 0, countConcealedContainsOneOrNine = 0;
        int countRevealedWords = 0, countConcealedWords = 0;
        int countRevealedNoWonder = 0, countConcealedNoWonder = 0;

        for (List<Meld> revealedMelds : List.of(
                ctx.getGroupedHand().getRevealedPairs(),
                ctx.getGroupedHand().getRevealedPongs(),
                ctx.getGroupedHand().getRevealedKongs()
        )) {
            for (Meld revealedMeld : revealedMelds) {
                switch (isWonderPairOrPongOrKong(revealedMeld)) {
                    case 0 -> {
                        countRevealedNoWonder++;
                    }
                    case 1 -> {
                        countRevealedFullOneOrNine++;
                    }
                    case 2 -> {
                        countRevealedWords++;
                    }
                }
            }
        }

        for (List<Meld> concealedMelds : List.of(
                ctx.getGroupedHand().getConcealedPairs(),
                ctx.getGroupedHand().getConcealedPongs(),
                ctx.getGroupedHand().getConcealedKongs()
        )) {
            for (Meld concealedMeld : concealedMelds) {
                switch (isWonderPairOrPongOrKong(concealedMeld)) {
                    case 0 -> {
                        countConcealedNoWonder++;
                    }
                    case 1 -> {
                        countConcealedFullOneOrNine++;
                    }
                    case 2 -> {
                        countConcealedWords++;
                    }
                }
            }
        }

        for (Meld revealedSheung : ctx.getGroupedHand().getRevealedSheungs()) {
            if (isWonderSheung(revealedSheung)) {
                countRevealedContainsOneOrNine++;
            } else {
                countRevealedNoWonder++;
            }
        }

        for (Meld concealedSheung : ctx.getGroupedHand().getConcealedSheungs()) {
            if (isWonderSheung(concealedSheung)) {
                countConcealedContainsOneOrNine++;
            } else {
                countConcealedNoWonder++;
            }
        }

        int countWords = countConcealedWords + countRevealedWords;
        int countFullOneOrNine = countConcealedFullOneOrNine + countRevealedFullOneOrNine;
        int countContainsOneOrNine = countConcealedContainsOneOrNine + countRevealedContainsOneOrNine;
        int countNoWonder = countConcealedNoWonder + countRevealedNoWonder;

        int countHasWonder = countWords + countFullOneOrNine + countContainsOneOrNine;

        noWondersMatcher(ctx, countHasWonder);
        if (allWondersNoWordsMatcher(ctx, countWords + countContainsOneOrNine + countNoWonder)) return;
        if (allWondersMatcher(ctx, countContainsOneOrNine + countNoWonder)) return;
        if (allUsingWondersNoWordsMatcher(ctx, countWords + countNoWonder)) return;
        allUsingWonders(ctx, countNoWonder);
    }

    // =============== MATCHERS ===============

    private void noWondersMatcher(ScoringContext ctx, int countHasWonder) {
        if (countHasWonder == 0) {
            ctx.addScoringPattern(ScoringPattern.NO_WONDERS);
        }
    }

    private boolean allWondersNoWordsMatcher(ScoringContext ctx, int countNonFullOneOrNine) {
        if (countNonFullOneOrNine == 0) {
            ctx.addScoringPattern(ScoringPattern.ALL_WONDERS_NO_WORDS);
            return true;
        }
        return false;
    }

    private boolean allWondersMatcher(ScoringContext ctx, int countNonFullWonder) {
        if (countNonFullWonder == 0) {
            ctx.addScoringPattern(ScoringPattern.ALL_WONDERS);
            return true;
        }
        return false;
    }

    private boolean allUsingWondersNoWordsMatcher(ScoringContext ctx, int countNonHasOneOrNine) {
        if (countNonHasOneOrNine == 0) {
            ctx.addScoringPattern(ScoringPattern.ALL_USING_WONDERS_NO_WORDS);
            return true;
        }
        return false;
    }

    private boolean allUsingWonders(ScoringContext ctx, int countNonHasWonder) {
        if (countNonHasWonder == 0) {
            ctx.addScoringPattern(ScoringPattern.ALL_USING_WONDERS);
            return true;
        }
        return false;
    }

    // =============== HELPERS ===============

    private boolean isWonderSheung(Meld meld) {
        int tileNum = meld.getStartingTile().getTileNum();
        return tileNum == 1 || tileNum == 7;
    }

    private int isWonderPairOrPongOrKong(Meld group) {
        return isWonder(group.getStartingTile());
    }

    private int isWonder(Tile tile) {
        if (isWord(tile)) return 2;
        if (isOneOrNine(tile)) return 1;
        return 0;
    }

    private boolean isOneOrNine(Tile tile) {
        return tile.getTileNum() == 1 || tile.getTileNum() == 9;
    }

    private boolean isWord(Tile tile) {
        return tile.getTileType().getClassification() == TileClassification.WORD;
    }
}
