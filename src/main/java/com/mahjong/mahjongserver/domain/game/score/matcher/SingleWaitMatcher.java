package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

public class SingleWaitMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        Tile winningTile = ctx.getWinningTile();
        Meld winningMeld = ctx.getWinningMeld();
        MeldType winningMeldType = winningMeld.getMeldType();

        int winningMeldTileNum = winningMeld.getStartingTile().getTileNum();
        TileType winningMeldTileType = winningMeld.getStartingTile().getTileType();

        if (winningMeldType == MeldType.PAIR) {
            boolean fakeSingleWait = false;

            sheungs: for (Meld sheung : ctx.getGroupedHand().getConcealedSheungs()) {
                if (sheung.getStartingTile().getTileType() == winningMeldTileType) {
                    int startingTileNum = sheung.getStartingTile().getTileNum();

                    if (startingTileNum == winningMeldTileNum - 3 || startingTileNum == winningMeldTileNum + 1) {
                        // e.g. 123 4(4), 6(6) 789
                        ctx.addScoringPattern(ScoringPattern.FAKE_SINGLE_WAIT);
                        fakeSingleWait = true;
                        break;

                    } else if (startingTileNum == winningMeldTileNum - 4) {
                        // e.g. 123 444 5(5)
                        for (Meld pong : ctx.getGroupedHand().getConcealedPongs()) {
                            Tile pongTile = pong.getStartingTile();
                            if (pongTile.getTileType() == winningMeldTileType
                                    && pongTile.getTileNum() == winningMeldTileNum - 1) {
                                ctx.addScoringPattern(ScoringPattern.FAKE_SINGLE_WAIT);
                                fakeSingleWait = true;
                                break sheungs;
                            }
                        }

                    } else if (startingTileNum == winningMeldTileNum + 2) {
                        // e.g. 5(5) 666 789
                        for (Meld pong : ctx.getGroupedHand().getConcealedPongs()) {
                            Tile pongTile = pong.getStartingTile();
                            if (pongTile.getTileType() == winningMeldTileType
                                    && pongTile.getTileNum() == winningMeldTileNum + 1) {
                                ctx.addScoringPattern(ScoringPattern.FAKE_SINGLE_WAIT);
                                fakeSingleWait = true;
                                break sheungs;
                            }
                        }
                    }
                }
            }

            if (!fakeSingleWait) ctx.addScoringPattern(ScoringPattern.SINGLE_WAIT);

        } else if (winningMeldType == MeldType.SHEUNG) {
            if (winningTile == winningMeld.getGroup().get(1)) {
                // e.g. 4(5)6
                ctx.addScoringPattern(ScoringPattern.SINGLE_WAIT);

            } else if (winningTile == winningMeld.getGroup().getLast()) {
                // e.g. 12(3)
                if (winningTile.getTileNum() == 3) {
                    boolean fakeSingleWait = false;

                    for (Meld sheung : ctx.getGroupedHand().getConcealedSheungs()) {
                        if (sheung.getStartingTile() == winningTile) {
                            // e.g. 12(3) 345
                            ctx.addScoringPattern(ScoringPattern.FAKE_SINGLE_WAIT);
                            fakeSingleWait = true;
                            break;
                        }
                    }

                    if (!fakeSingleWait) ctx.addScoringPattern(ScoringPattern.SINGLE_WAIT);
                }

            } else {
                // e.g. (7)89
                if (winningTile.getTileNum() == 7) {
                    boolean fakeSingleWait = false;

                    for (Meld sheung : ctx.getGroupedHand().getConcealedSheungs()) {
                        Tile sheungTile = sheung.getStartingTile();
                        if (sheungTile.getTileType() == winningMeldTileType && sheungTile.getTileNum() == 5) {
                            // e.g. 567 (7)89
                            ctx.addScoringPattern(ScoringPattern.FAKE_SINGLE_WAIT);
                            fakeSingleWait = true;
                            break;
                        }
                    }

                    if (!fakeSingleWait) ctx.addScoringPattern(ScoringPattern.SINGLE_WAIT);
                }
            }
        }
    }
}
