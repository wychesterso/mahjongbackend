package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ScoringResult {
    private final int score;
    private List<List<Tile>> concealedHandGrouping;
    private final List<ScoringPattern> scoringPatterns;

    public ScoringResult(int score,
                         List<List<Tile>> concealedHandGrouping,
                         List<ScoringPattern> scoringPatterns) {
        this.score = score;
        this.concealedHandGrouping = concealedHandGrouping;
        this.scoringPatterns = scoringPatterns;
    }

    public int getScore() {
        return score;
    }

    public List<List<Tile>> getConcealedHandGrouping() {
        return concealedHandGrouping;
    }

    public List<ScoringPattern> getScoringPatterns() {
        return scoringPatterns;
    }
}
