package com.mahjong.mahjongserver.domain.game.score.data;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoringContext {
    private final Game game;
    private final Seat winnerSeat;
    private GroupedHand groupedHand = null;

    private final List<ScoringPattern> scoringPatterns = new ArrayList<>();

    public ScoringContext(Game game, Seat winnerSeat, GroupedHand groupedHand) {
        this.game = game;
        this.winnerSeat = winnerSeat;
        this.groupedHand = groupedHand;
    }

// GAME DATA

    public Game getGame() {
        return game;
    }

    public GroupedHand getGroupedHand() {
        return groupedHand;
    }

    public boolean isSelfDraw() {
        return getWinnerSeat() == getLoserSeat();
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

    public Seat getLoserSeat() {
        return game.getCurrentSeat();
    }

// HAND AND TILES

    /**
     * Retrieves the tile used in the pair. Makes the assumption that the hand being checked is a regular hand with
     * only 1 pair.
     * @return the tile used in the pair.
     */
    public Tile getPairTile() {
        if (getPair() == null) return null;
        return getPair().getFirst();
    }

    /**
     * Retrieves the tile pair. Makes the assumption that the hand being checked is a regular hand with only 1 pair.
     * @return the tile pair.
     */
    public List<Tile> getPair() {
        if (getConcealedPairs().isEmpty()) return null;
        return getConcealedPairs().getFirst();
    }

    public Set<Tile> getFlowers() {
        return groupedHand.getFlowers();
    }

    public List<List<Tile>> getConcealedPairs() {
        return groupedHand.getConcealedPairs();
    }

    public List<List<Tile>> getConcealedSheungs() {
        return groupedHand.getConcealedSheungs();
    }

    public List<List<Tile>> getRevealedSheungs() {
        return groupedHand.getRevealedSheungs();
    }

    public List<List<Tile>> getConcealedPongs() {
        return groupedHand.getConcealedPongs();
    }

    public List<List<Tile>> getRevealedPongs() {
        return groupedHand.getRevealedPongs();
    }

    public List<List<Tile>> getBrightKongs() {
        return groupedHand.getBrightKongs();
    }

    public List<List<Tile>> getDarkKongs() {
        return groupedHand.getDarkKongs();
    }

// COMBINED

    public List<List<Tile>> getAllSheungs() {
        List<List<Tile>> allSheungs = new ArrayList<>();
        allSheungs.addAll(getConcealedSheungs());
        allSheungs.addAll(getRevealedSheungs());
        return allSheungs;
    }

    public List<List<Tile>> getAllPongs() {
        List<List<Tile>> allPongs = new ArrayList<>();
        allPongs.addAll(getConcealedPongs());
        allPongs.addAll(getRevealedPongs());
        return allPongs;
    }

    public List<List<Tile>> getAllKongs() {
        List<List<Tile>> allKongs = new ArrayList<>();
        allKongs.addAll(getBrightKongs());
        allKongs.addAll(getDarkKongs());
        return allKongs;
    }

    public List<List<Tile>> getAllPongsAndKongs() {
        List<List<Tile>> allPongsAndKongs = new ArrayList<>();
        allPongsAndKongs.addAll(getAllPongs());
        allPongsAndKongs.addAll(getAllKongs());
        return allPongsAndKongs;
    }




// PATTERN MANAGEMENT AND SCORING

    public List<ScoringPattern> getScoringPatterns() {
        return scoringPatterns;
    }

    public void addScoringPattern(ScoringPattern scoringPattern) {
        scoringPatterns.add(scoringPattern);
    }

    public void addScoringPatterns(List<ScoringPattern> scoringPatterns) {
        this.scoringPatterns.addAll(scoringPatterns);
    }

    public int getScore() {
        int score = 0;
        for (ScoringPattern scoringPattern : scoringPatterns) {
            score += scoringPattern.getValue();
        }
        return score;
    }
}
