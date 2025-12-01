package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.grouping.GroupedHand;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.grouping.GroupedHandBuilder;
import com.mahjong.mahjongserver.domain.game.score.grouping.HandGrouper;
import com.mahjong.mahjongserver.domain.game.score.matcher.*;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class TaiwaneseSixteenScoreCalculator implements ScoreCalculator {

//============================== ENTRY POINT ==============================//

    public ScoringContext calculateScore(Game game, Seat winnerSeat, int lumZhongCount) {
        // get table and hand
        Table table = game.getTable();
        Hand hand = table.getHand(winnerSeat);

        // get all valid groupings for concealed tiles
        List<Tile> concealedTiles = hand.getConcealedTiles();
        List<GroupedHandBuilder> groupings = HandGrouper.getValidGroupings(concealedTiles, game.getWinningTile());
        if (groupings.isEmpty()) {
            // crash out
            System.out.println("[ScoreCalculator] No groupings found!");
            System.out.println("concealedTiles: " + concealedTiles + ", winningTile: " + game.getWinningTile());
            throw new IllegalStateException();
        }

        ScoringContext bestScoringContext = null;

        // determine which grouping gives the best score
        for (GroupedHandBuilder builder : groupings) {
            GroupedHand groupedHand = new GroupedHand(builder, hand, game.getCurrentSeat() == winnerSeat);

            ScoringContext scoringContext = new ScoringContext(game, winnerSeat, lumZhongCount, groupedHand);
            calculateScoreForGrouping(scoringContext);

            if (bestScoringContext == null || scoringContext.getScore() > bestScoringContext.getScore()) {
                bestScoringContext = scoringContext;
            }
        }

        return bestScoringContext;
    }

//============================== SCORES ==============================//

    private static final List<ScoringPatternMatcher> matchers = List.of(
            new FlowerMatcher(),
            new GeneralMatcher(),
            new GameActionMatcher(),
            new FirstDiscardsMatcher(),
            new RemainingTilesMatcher(),

            new ThirteenWondersMatcher(),
            new SixteenDisjointMatcher(),
            new LikKuLikKuMatcher(),

            new KongMatcher(),
            new WindTileMatcher(),
            new DragonTileMatcher(),
            new SuitsMatcher(),

            new SingleWaitMatcher(),
            new LoSiuMatcher(),
            new SheungsMatcher(),
            new PongsMatcher(),
            new ConcealedPongsMatcher(),
            new AllMeldedMatcher(),
            new FourTilesNGroupsMatcher(),

            new DragonMatcher(),
            new WondersMatcher(),

            new ChickenHandMatcher(),
            new ZhongMatcher()
    );

    private void calculateScoreForGrouping(ScoringContext scoringContext) {
        // accumulate scoring patterns inside scoringContext
        for (ScoringPatternMatcher matcher : matchers) {
            matcher.match(scoringContext);
        }
    }
}
