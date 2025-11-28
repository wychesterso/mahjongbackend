package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.*;

public class FourTilesNGroupsMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        Map<Tile, Integer> numOccurences = new HashMap<>();
        Map<Tile, Integer> numGroups = new HashMap<>();

        for (Meld meld : ctx.getGroupedHand().getAllMelds()) {
            Set<Tile> seen = new HashSet<>();

            Tile startingTile = meld.getStartingTile();
            seen.add(startingTile);

            switch (meld.getMeldType()) {
                case MeldType.PAIR -> {
                    numOccurences.put(startingTile, numOccurences.getOrDefault(startingTile, 0) + 2);
                }
                case MeldType.SHEUNG -> {
                    numOccurences.put(startingTile, numOccurences.getOrDefault(startingTile, 0) + 1);

                    Tile tile2 = Tile.values()[startingTile.ordinal() + 1];
                    seen.add(tile2);
                    numOccurences.put(tile2, numOccurences.getOrDefault(tile2, 0) + 1);

                    Tile tile3 = Tile.values()[startingTile.ordinal() + 2];
                    seen.add(tile3);
                    numOccurences.put(tile3, numOccurences.getOrDefault(tile3, 0) + 1);
                }
                case MeldType.PONG -> {
                    numOccurences.put(startingTile, numOccurences.getOrDefault(startingTile, 0) + 3);
                }
                case MeldType.KONG -> {
                    numOccurences.put(startingTile, numOccurences.getOrDefault(startingTile, 0) + 4);
                }
            }

            for (Tile tile : seen) {
                numGroups.put(tile, numGroups.getOrDefault(tile, 0) + 1);
            }
        }

        for (Map.Entry<Tile, Integer> entry : numGroups.entrySet()) {
            if (numOccurences.get(entry.getKey()) == 4) {
                switch (entry.getValue()) {
                    case 4 -> {ctx.addScoringPattern(ScoringPattern.FOUR_TILES_FOUR_GROUPS);}
                    case 3 -> {ctx.addScoringPattern(ScoringPattern.FOUR_TILES_THREE_GROUPS);}
                    case 2 -> {ctx.addScoringPattern(ScoringPattern.FOUR_TILES_TWO_GROUPS);}
                }
            }
        }
    }
}
