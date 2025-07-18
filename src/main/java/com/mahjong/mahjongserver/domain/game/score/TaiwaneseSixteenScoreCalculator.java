package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class TaiwaneseSixteenScoreCalculator implements ScoreCalculator {

//============================== ENTRY POINT ==============================//

    public ScoringResult calculateScore(Game game, Seat winnerSeat) {
        // get table and hand
        Table table = game.getTable();
        Hand hand = table.getHand(winnerSeat);

        // get all valid groupings for concealed tiles
        List<Tile> concealedTiles = hand.getConcealedTiles();
        List<List<List<Tile>>> groupings = HandGrouper.getValidGroupings(concealedTiles);

        // get game info
        Seat loserSeat = game.getCurrentSeat();
        boolean selfDraw = winnerSeat == loserSeat;
        Tile winningTile = hand.getLastDrawnTile();

        ScoringResult bestScoringResult = null;

        // determine which grouping gives the best score
        for (List<List<Tile>> grouping : groupings) {
            ScoringResult scoringResult = calculateScoreForGrouping(grouping, hand, winningTile, selfDraw);
            if (bestScoringResult == null || scoringResult.getScore() > bestScoringResult.getScore()) {
                bestScoringResult = scoringResult;
            }
        }

        return bestScoringResult;
    }

//============================== SCORES ==============================//

    private ScoringResult calculateScoreForGrouping(List<List<Tile>> grouping,
                                          Hand hand,
                                          Tile winningTile,
                                          boolean selfDraw) {
        // get groupings
        List<List<Tile>> concealedPairs = HandGrouper.getConcealedPairs(grouping);
        List<List<Tile>> concealedSheungs = HandGrouper.getConcealedSheungs(grouping);
        List<List<Tile>> revealedSheungs = hand.getSheungs();
        List<List<Tile>> concealedPongs = HandGrouper.getConcealedPongs(grouping);
        List<List<Tile>> revealedPongs = hand.getPongs();
        List<List<Tile>> brightKongs = hand.getBrightKongs();
        List<List<Tile>> darkKongs = hand.getDarkKongs();

        // TODO: accumulate scoring patterns
        List<ScoringPattern> scoringPatterns = new ArrayList<>();

        // tally scores
        int score = 0;
        for (ScoringPattern scoringPattern : scoringPatterns) {
            score += scoringPattern.getValue();
        }

        return new ScoringResult(score, grouping, scoringPatterns);
    }
}
