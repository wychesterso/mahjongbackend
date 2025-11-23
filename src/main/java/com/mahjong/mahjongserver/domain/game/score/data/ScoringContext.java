package com.mahjong.mahjongserver.domain.game.score.data;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.grouping.GroupedHand;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoringContext {

    // base info

    private final Game game;
    private final Seat winnerSeat;
    private GroupedHand groupedHand = null;

    // preprocessed groupings

    private final List<List<Tile>> sheungs = new ArrayList<>();
    private final List<List<Tile>> pongs = new ArrayList<>();
    private final List<List<Tile>> kongs = new ArrayList<>();
    private final List<List<Tile>> pongsAndKongs = new ArrayList<>();

    private final List<List<Tile>> concealedPongsAndKongs = new ArrayList<>();
    private final List<List<Tile>> revealedPongsAndKongs = new ArrayList<>();

    private final List<List<Tile>> concealedMelds = new ArrayList<>();
    private final List<List<Tile>> revealedMelds = new ArrayList<>();
    private final List<List<Tile>> melds = new ArrayList<>();

    private final List<List<Tile>> circles = new ArrayList<>();
    private final List<List<Tile>> bamboos = new ArrayList<>();
    private final List<List<Tile>> millions = new ArrayList<>();
    private final List<List<Tile>> winds = new ArrayList<>();
    private final List<List<Tile>> dragons = new ArrayList<>();

    private final List<List<Tile>> numberTiles = new ArrayList<>();
    private final List<List<Tile>> words = new ArrayList<>();

    // resulting scoring pattern list
    private final List<ScoringPattern> scoringPatterns = new ArrayList<>();

    /**
     * Creates a new ScoringContext instance.
     * @param game the game instance.
     * @param winnerSeat the seat of the winner.
     * @param groupedHand the hand grouping to calculate score for.
     */
    public ScoringContext(Game game, Seat winnerSeat, GroupedHand groupedHand) {
        this.game = game;
        this.winnerSeat = winnerSeat;
        this.groupedHand = groupedHand;

        preprocessGroups();
        preprocessSuits();
    }

    // ============================== PREPROCESSING ==============================

    private void preprocessGroups() {
        sheungs.addAll(groupedHand.getConcealedSheungs());
        sheungs.addAll(groupedHand.getRevealedSheungs());

        pongs.addAll(groupedHand.getConcealedPongs());
        pongs.addAll(groupedHand.getRevealedPongs());

        kongs.addAll(groupedHand.getDarkKongs());
        kongs.addAll(groupedHand.getBrightKongs());

        pongsAndKongs.addAll(pongs);
        pongsAndKongs.addAll(kongs);

        concealedPongsAndKongs.addAll(groupedHand.getConcealedPongs());
        concealedPongsAndKongs.addAll(groupedHand.getDarkKongs());
        revealedPongsAndKongs.addAll(groupedHand.getRevealedPongs());
        revealedPongsAndKongs.addAll(groupedHand.getBrightKongs());

        concealedMelds.addAll(groupedHand.getConcealedSheungs());
        concealedMelds.addAll(concealedPongsAndKongs);

        revealedMelds.addAll(groupedHand.getRevealedSheungs());
        revealedMelds.addAll(revealedPongsAndKongs);

        melds.addAll(concealedMelds);
        melds.addAll(revealedMelds);
    }

    private void preprocessSuits() {
        for (List<Tile> group : groupedHand.getConcealedPairs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getConcealedSheungs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getRevealedSheungs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getConcealedPongs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getRevealedPongs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getBrightKongs()) {
            preprocessGroupInSuit(group);
        }
        for (List<Tile> group : groupedHand.getDarkKongs()) {
            preprocessGroupInSuit(group);
        }

        numberTiles.addAll(circles);
        numberTiles.addAll(bamboos);
        numberTiles.addAll(millions);

        words.addAll(winds);
        words.addAll(dragons);
    }

    private void preprocessGroupInSuit(List<Tile> group) {
        switch (group.getFirst().getTileType()) {
            case CIRCLE -> {circles.add(group);}
            case BAMBOO -> {bamboos.add(group);}
            case MILLION -> {millions.add(group);}
            case WIND -> {winds.add(group);}
            case DRAGON -> {dragons.add(group);}
        }
    }

    // ============================== GAME DATA ==============================

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

    // ============================== COUNTS ==============================

    public int numPairs() {
        return getConcealedPairs().size();
    }

    public int numConcealedSheungs() {
        return getConcealedSheungs().size();
    }

    public int numRevealedSheungs() {
        return getRevealedSheungs().size();
    }

    public int numSheungs() {
        return sheungs.size();
    }

    public int numConcealedPongs() {
        return getConcealedPongs().size();
    }

    public int numRevealedPongs() {
        return getRevealedPongs().size();
    }

    public int numPongs() {
        return pongs.size();
    }

    public int numBrightKongs() {
        return getBrightKongs().size();
    }

    public int numDarkKongs() {
        return getDarkKongs().size();
    }

    public int numKongs() {
        return kongs.size();
    }

    public int numPongsAndKongs() {
        return pongsAndKongs.size();
    }

    // ============================== GROUPINGS ==============================

    public Tile getWinningTile() {
        return game.getWinningTile();
    }

    public List<Tile> getWinningGroup() {
        return groupedHand.getWinningGroup();
    }

    public MeldType getWinningGroupType() {
        return groupedHand.getWinningGroupType();
    }

    public List<List<Tile>> getPairs() {
        return groupedHand.getConcealedPairs();
    }

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

    public List<List<Tile>> getAllSheungs() {
        return sheungs;
    }

    public List<List<Tile>> getAllPongs() {
        return pongs;
    }

    public List<List<Tile>> getAllKongs() {
        return kongs;
    }

    public List<List<Tile>> getConcealedPongsAndKongs() {
        return concealedPongsAndKongs;
    }

    public List<List<Tile>> getRevealedPongsAndKongs() {
        return revealedPongsAndKongs;
    }

    public List<List<Tile>> getAllPongsAndKongs() {
        return pongsAndKongs;
    }

    public List<List<Tile>> getRevealedMelds() {
        return revealedMelds;
    }

    public List<List<Tile>> getConcealedMelds() {
        return concealedMelds;
    }

    public List<List<Tile>> getAllMelds() {
        return melds;
    }

    // ============================== SUITS ==============================

    public List<List<Tile>> getCircles() {
        return circles;
    }

    public int numCircleGroups() {
        return getCircles().size();
    }

    public List<List<Tile>> getBamboos() {
        return bamboos;
    }

    public int numBambooGroups() {
        return getBamboos().size();
    }

    public List<List<Tile>> getMillions() {
        return millions;
    }

    public int numMillionGroups() {
        return getMillions().size();
    }

    public List<List<Tile>> getNumberTiles() {
        return numberTiles;
    }

    public int numNumberTileGroups() {
        return getNumberTiles().size();
    }

    public List<List<Tile>> getWinds() {
        return winds;
    }

    public int numWindGroups() {
        return getWinds().size();
    }

    public List<List<Tile>> getDragons() {
        return dragons;
    }

    public int numDragonGroups() {
        return getDragons().size();
    }

    public List<List<Tile>> getWords() {
        return words;
    }

    public int numWordGroups() {
        return getWords().size();
    }

    // ============================== SCORING LOG ==============================

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
