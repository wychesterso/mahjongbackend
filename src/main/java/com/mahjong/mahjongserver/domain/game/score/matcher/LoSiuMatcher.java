package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

import java.util.List;

public class LoSiuMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<Meld> sheungs = ctx.getGroupedHand().getSheungs();
        int numSheungs = sheungs.size();
        
        for (int i = 0; i < numSheungs; i++) {
            for (int j = i + 1; j < numSheungs; j++) {
                Meld meld1 = sheungs.get(i);
                Tile tile1 = meld1.getStartingTile();
                Meld meld2 = sheungs.get(j);
                Tile tile2 = meld2.getStartingTile();

                if (tile1.getTileType() == tile2.getTileType()
                        && tile1.getTileNum() == 1 && tile2.getTileNum() == 7) {
                    ctx.addScoringPattern(ScoringPattern.LO_SIU);
                    return;
                }
            }
        }

        List<Meld> pongsAndKongs = ctx.getGroupedHand().getPongsAndKongs();
        int numPongsAndKongs = pongsAndKongs.size();

        for (int i = 0; i < numPongsAndKongs; i++) {
            Meld meld1 = pongsAndKongs.get(i);
            Tile tile1 = meld1.getStartingTile();
            if (tile1.getTileType().getClassification() != TileClassification.REGULAR) continue;

            for (int j = i + 1; j < numPongsAndKongs; j++) {
                Meld meld2 = pongsAndKongs.get(j);
                Tile tile2 = meld2.getStartingTile();

                if (tile1.getTileType() == tile2.getTileType()
                        && Math.abs(tile1.getTileNum() - tile2.getTileNum()) == 8) {
                    ctx.addScoringPattern(ScoringPattern.LO_SIU);
                    break;
                }
            }
        }
    }
}
