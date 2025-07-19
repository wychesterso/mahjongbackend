package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.List;

public interface PlayerDecisionHandler {
    void promptDecision(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder,
                        List<Decision> availableOptions, List<List<Tile>> sheungCombos);
    void promptDiscard(PlayerContext ctx, TableDTO table);
    void promptDiscardOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile);
    void promptEndGameDecision(PlayerContext ctx, Room room);
}
