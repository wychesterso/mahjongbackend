package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.data.GroupedHand;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.matcher.*;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class TaiwaneseSixteenScoreCalculator implements ScoreCalculator {

//============================== ENTRY POINT ==============================//

    public ScoringContext calculateScore(Game game, Seat winnerSeat) {
        // get table and hand
        Table table = game.getTable();
        Hand hand = table.getHand(winnerSeat);

        // get all valid groupings for concealed tiles
        List<Tile> concealedTiles = hand.getConcealedTiles();
        List<List<List<Tile>>> groupings = HandGrouper.getValidGroupings(concealedTiles);

        ScoringContext bestScoringContext = null;

        // determine which grouping gives the best score
        for (List<List<Tile>> grouping : groupings) {
            GroupedHand groupedHand = new GroupedHand(grouping, hand);
            ScoringContext scoringContext = new ScoringContext(game, winnerSeat, groupedHand);
            calculateScoreForGrouping(scoringContext);

            if (bestScoringContext == null || scoringContext.getScore() > bestScoringContext.getScore()) {
                bestScoringContext = scoringContext;
            }
        }

        return bestScoringContext;
    }

//============================== SCORES ==============================//

    private static final List<ScoringPatternMatcher> matchers = List.of(
            new GeneralMatcher(),
            new FlowerMatcher(),
            new WindMatcher(),
            new DragonMatcher(),
            new AllSheungsMatcher(),
            new FrontAndBackMatcher()
    );

    private void calculateScoreForGrouping(ScoringContext scoringContext) {
        // get groupings
        GroupedHand groupedHand = scoringContext.getGroupedHand();

        // accumulate scoring patterns inside scoringContext
        for (ScoringPatternMatcher matcher : matchers) {
            matcher.match(scoringContext);
        }
    }
}
