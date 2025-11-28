package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheungsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        matchAllSheungs(ctx);
        matchSheungPatterns(ctx);
    }

    public void matchAllSheungs(ScoringContext ctx) {
        if (ctx.getGroupedHand().numSheungs() == 5) {
            if (ctx.ifExistsThenRemoveScoringPattern(ScoringPattern.NO_WORDS_OR_FLOWERS)) {
                ctx.addScoringPattern(ScoringPattern.ALL_SHEUNGS_NO_WORDS_OR_FLOWERS);
            } else {
                ctx.addScoringPattern(ScoringPattern.ALL_SHEUNGS);
            }
        }
    }
    private void matchSheungPatterns(ScoringContext ctx) {
        List<Meld> sheungs = ctx.getGroupedHand().getSheungs();
        if (sheungs.isEmpty()) return;

        Map<Integer, List<Meld>> byNumber = new HashMap<>();

        for (Meld sheung : sheungs) {
            // group into starting nums
            byNumber.computeIfAbsent(sheung.getStartingTile().getTileNum(), k -> new ArrayList<>()).add(sheung);
        }

        for (List<Meld> numGrouping : byNumber.values()) {
            int numSheungsInGroup = numGrouping.size();

            // if group of 5 found, then guaranteed that no others exist
            if (matchFiveEncounters(ctx, numSheungsInGroup)) return;

            Map<TileType, Integer> typeCounts = new HashMap<>();
            int maxCount = 0;

            for (Meld sheung : numGrouping) {
                // further group into tile types
                TileType type = sheung.getStartingTile().getTileType();
                int count = typeCounts.getOrDefault(type, 0) + 1;

                maxCount = Math.max(maxCount, count);

                typeCounts.put(type, count);
            }

            // if group of 4 found, then guaranteed that no others exist
            if (matchFourIdenticalSheungs(ctx, maxCount)) return;
            if (matchFourEncounters(ctx, numSheungsInGroup)) return;

            // deal with groups of 3 or 2
            if (matchThreeIdenticalSheungs(ctx, maxCount)) continue;
            if (matchThreeEncounters(ctx, numSheungsInGroup)) continue;
            if (matchTwoIdenticalSheungs(ctx, maxCount)) continue;
            matchTwoEncounters(ctx, numSheungsInGroup);
        }
    }

    //======================================================================
    // ENCOUNTERS (same num, diff suit)
    //======================================================================

    private boolean matchFiveEncounters(ScoringContext ctx, int count) {
        if (count >= 5) {
            ctx.addScoringPattern(ScoringPattern.FIVE_ENCOUNTERS);
            return true;
        }
        return false;
    }

    private boolean matchFourEncounters(ScoringContext ctx, int count) {
        if (count >= 4) {
            ctx.addScoringPattern(ScoringPattern.FOUR_ENCOUNTERS);
            return true;
        }
        return false;
    }

    private boolean matchThreeEncounters(ScoringContext ctx, int count) {
        if (count >= 3) {
            ctx.addScoringPattern(ScoringPattern.THREE_ENCOUNTERS);
            return true;
        }
        return false;
    }

    private boolean matchTwoEncounters(ScoringContext ctx, int count) {
        if (count >= 2) {
            ctx.addScoringPattern(ScoringPattern.TWO_ENCOUNTERS);
            return true;
        }
        return false;
    }

    //======================================================================
    // IDENTICAL SHEUNGS (same num, same suit)
    //======================================================================

    private boolean matchFourIdenticalSheungs(ScoringContext ctx, int count) {
        if (count >= 4) {
            ctx.addScoringPattern(ScoringPattern.FOUR_IDENTICAL_SHEUNGS);
            return true;
        }
        return false;
    }

    private boolean matchThreeIdenticalSheungs(ScoringContext ctx, int count) {
        if (count >= 3) {
            ctx.addScoringPattern(ScoringPattern.THREE_IDENTICAL_SHEUNGS);
            return true;
        }
        return false;
    }

    private boolean matchTwoIdenticalSheungs(ScoringContext ctx, int count) {
        if (count >= 2) {
            ctx.addScoringPattern(ScoringPattern.TWO_IDENTICAL_SHEUNGS);
            return true;
        }
        return false;
    }
}
