package com.mahjong.mahjongserver.domain.game.score.data;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.grouping.GroupedHand;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.*;

public class ScoringContext {

    // game and hand information
    private final Game game;
    private final Seat winnerSeat;
    private final boolean isSelfDraw;
    private final int lumZhongCount;
    private final GroupedHand groupedHand;

    // resulting scoring pattern list
    private final List<ScoringPattern> scoringPatterns = new ArrayList<>();

    /**
     * Creates a new ScoringContext instance.
     * @param game the game instance.
     * @param winnerSeat the seat of the winner.
     * @param groupedHand the hand grouping to calculate score for.
     */
    public ScoringContext(Game game, Seat winnerSeat, int lumZhongCount, GroupedHand groupedHand) {
        this.game = game;
        this.winnerSeat = winnerSeat;
        this.isSelfDraw = winnerSeat == game.getCurrentSeat();
        this.lumZhongCount = lumZhongCount;
        this.groupedHand = groupedHand;
    }

    // ============================== GAME DATA ==============================

    public Game getGame() {
        return game;
    }

    public GroupedHand getGroupedHand() {
        return groupedHand;
    }

    public boolean isSelfDraw() {
        return isSelfDraw;
    }

    public int getLumZhongCount() {
        return lumZhongCount;
    }

    public Seat getWindSeat() {
        return game.getWindSeat();
    }

    public Seat getZhongSeat() {
        return game.getZhongSeat();
    }

    public Seat getWinnerSeat() {
        return winnerSeat;
    }

    /**
     * Returns the seat of the loser, provided that the game was not a self draw.
     * @return the loser's seat.
     */
    public Seat getLoserSeat() {
        return game.getCurrentSeat();
    }

    public Tile getWinningTile() {
        return game.getWinningTile();
    }

    public Meld getWinningMeld() {
        return groupedHand.getWinningMeld();
    }

    // ============================== SCORING LOG ==============================

    public List<ScoringPattern> getScoringPatterns() {
        return scoringPatterns;
    }

    public boolean containsScoringPattern(ScoringPattern pattern) {
        return scoringPatterns.contains(pattern);
    }

    public boolean containsScoringPatterns(Collection<ScoringPattern> patterns) {
        return new HashSet<>(scoringPatterns).containsAll(patterns);
    }

    public void addScoringPattern(ScoringPattern pattern) {
        scoringPatterns.add(pattern);
    }

    public void addScoringPatterns(Collection<ScoringPattern> patterns) {
        scoringPatterns.addAll(patterns);
    }

    public boolean ifExistsThenRemoveScoringPattern(ScoringPattern pattern) {
        if (containsScoringPattern(pattern)) {
            removeScoringPattern(pattern);
            return true;
        }
        return false;
    }

    public void removeScoringPattern(ScoringPattern pattern) {
        scoringPatterns.remove(pattern);
    }

    public int getScore() {
        int score = 0;
        for (ScoringPattern scoringPattern : scoringPatterns) {
            score += scoringPattern.getValue();
        }
        return score;
    }
}
