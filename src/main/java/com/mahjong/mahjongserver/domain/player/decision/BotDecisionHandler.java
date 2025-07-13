package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.table.TableDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BotDecisionHandler implements PlayerDecisionHandler {

    @Override
    public void promptWinOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {

    }

    @Override
    public void promptWinOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {

    }

    @Override
    public void promptSheung(PlayerContext ctx, TableDTO table, Tile discardedTile) {

    }

    @Override
    public void promptPong(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {

    }

    @Override
    public void promptBrightKongOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {

    }

    public void promptBrightKongOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {

    }

    public void promptDarkKong(PlayerContext ctx, TableDTO table, Tile drawnTile) {

    }

    public void promptSheungCombo(PlayerContext ctx, Tile discardedTile, List<List<Tile>> validCombos) {

    }

    public void promptDiscard(PlayerContext ctx, TableDTO table) {

    }

    public void promptDiscardOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {

    }
}
