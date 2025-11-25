package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

import java.util.List;

public class LoSiuMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        List<List<Tile>> sheungs = ctx.getAllSheungs();
        int numSheungs = ctx.numSheungs();
        
        for (int i = 0; i < numSheungs; i++) {
            for (int j = i + 1; j < numSheungs; j++) {
                List<Tile> group1 = sheungs.get(i);
                Tile tile1 = group1.getFirst();
                List<Tile> group2 = sheungs.get(j);
                Tile tile2 = group2.getFirst();
                if (tile1.getTileType() == tile2.getTileType()
                        && tile1.getTileNum() == 1 && tile2.getTileNum() == 7) {
                    ctx.addScoringPattern(ScoringPattern.LO_SIU);
                    return;
                }
            }
        }

        List<List<Tile>> pongsAndKongs = ctx.getAllPongsAndKongs();
        int numPongsAndKongs = pongsAndKongs.size();

        for (int i = 0; i < numPongsAndKongs; i++) {
            List<Tile> group1 = pongsAndKongs.get(i);
            Tile tile1 = group1.getFirst();
            if (tile1.getTileType().getClassification() != TileClassification.REGULAR) continue;

            for (int j = i + 1; j < numPongsAndKongs; j++) {
                List<Tile> group2 = pongsAndKongs.get(j);
                Tile tile2 = group2.getFirst();

                if (tile1.getTileType() == tile2.getTileType()
                        && Math.abs(tile1.getTileNum() - tile2.getTileNum()) == 8) {
                    ctx.addScoringPattern(ScoringPattern.LO_SIU);
                    break;
                }
            }
        }
    }
}
