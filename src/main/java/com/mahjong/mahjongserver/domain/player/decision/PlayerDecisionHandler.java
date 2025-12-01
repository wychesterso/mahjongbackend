package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.GameStateDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.List;

public interface PlayerDecisionHandler {
    void promptDecisionOnDraw(PlayerContext ctx, GameStateDTO state, Tile drawnTile, List<Decision> availableOptions,
                              List<Tile> availableBrightKongs, List<Tile> availableDarkKongs);
    void promptDecisionOnDiscard(PlayerContext ctx, GameStateDTO state, Tile discardedTile, Seat discarder,
                                 List<Decision> availableOptions, List<List<Tile>> sheungCombos);
    void promptDiscard(PlayerContext ctx, GameStateDTO state);
    void promptDiscardOnDraw(PlayerContext ctx, GameStateDTO state, Tile drawnTile);
    void promptEndGameDecision(PlayerContext ctx, Room room);
}
