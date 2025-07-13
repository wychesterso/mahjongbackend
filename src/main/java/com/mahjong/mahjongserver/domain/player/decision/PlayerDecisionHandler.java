package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.TableDTO;

import java.util.List;

public interface PlayerDecisionHandler {
    void promptWinOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder);
    void promptWinOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile);
    void promptSheung(PlayerContext ctx, TableDTO table, Tile discardedTile);
    void promptPong(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder);
    void promptBrightKongOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder);
    void promptBrightKongOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile);
    void promptDarkKong(PlayerContext ctx, TableDTO table, Tile drawnTile);
    void promptSheungCombo(PlayerContext ctx, Tile discardedTile, List<List<Tile>> validCombos);
    void promptDiscard(PlayerContext ctx, TableDTO table);
    void promptDiscardOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile);
}
