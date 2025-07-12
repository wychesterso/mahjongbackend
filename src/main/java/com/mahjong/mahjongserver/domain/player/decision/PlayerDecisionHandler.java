package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.dto.BoardStateDTO;
import com.mahjong.mahjongserver.game.PlayerContext;

import java.util.List;

public interface PlayerDecisionHandler {
    void promptWin(PlayerContext ctx, BoardStateDTO boardState);
    void promptWin(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptSheung(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptPong(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptDarkKong(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptBrightKong(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptBrightKongNoDraw(PlayerContext ctx, Tile tile, BoardStateDTO boardState);
    void promptSheungCombo(PlayerContext ctx, List<List<Tile>> validSheungs);
    void promptDiscard(PlayerContext ctx, BoardStateDTO boardState, List<Tile> discardedTiles);
    void promptDiscardNoDraw(PlayerContext ctx, BoardStateDTO boardState, List<Tile> discardedTiles);
}