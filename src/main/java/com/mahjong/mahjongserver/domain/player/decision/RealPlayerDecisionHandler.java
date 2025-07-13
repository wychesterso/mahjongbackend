package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.table.TableDTO;
import com.mahjong.mahjongserver.domain.core.GameEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RealPlayerDecisionHandler implements PlayerDecisionHandler {

    private final GameEventPublisher publisher;

    public RealPlayerDecisionHandler(GameEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void promptWinOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_win_on_discard",
                Map.of(
                        "table", table,
                        "discarded_tile", discardedTile,
                        "discarder", discarder
                )
        );
    }

    @Override
    public void promptWinOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_win_on_draw",
                Map.of(
                        "table", table,
                        "drawn_tile", drawnTile
                )
        );
    }

    @Override
    public void promptSheung(PlayerContext ctx, TableDTO table, Tile discardedTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_sheung",
                Map.of(
                        "table", table,
                        "discarded_tile", discardedTile
                )
        );
    }

    @Override
    public void promptPong(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_pong",
                Map.of(
                        "table", table,
                        "discarded_tile", discardedTile,
                        "discarder", discarder
                )
        );
    }

    @Override
    public void promptBrightKongOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_bright_kong_on_discard",
                Map.of(
                        "table", table,
                        "discarded_tile", discardedTile,
                        "discarder", discarder
                )
        );
    }

    public void promptBrightKongOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_bright_kong_on_draw",
                Map.of(
                        "table", table,
                        "drawn_tile", drawnTile
                )
        );
    }

    public void promptDarkKong(PlayerContext ctx, TableDTO table, Tile drawnTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_dark_kong",
                Map.of(
                        "table", table,
                        "drawn_tile", drawnTile
                )
        );
    }

    public void promptSheungCombo(PlayerContext ctx, Tile discardedTile, List<List<Tile>> validCombos) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_sheung_combo",
                Map.of(
                        "discarded_tile", discardedTile,
                        "valid_combos", validCombos
                )
        );
    }

    public void promptDiscard(PlayerContext ctx, TableDTO table) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard",
                Map.of(
                        "table", table
                )
        );
    }

    public void promptDiscardOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard_on_draw",
                Map.of(
                        "table", table,
                        "drawn_tile", drawnTile
                )
        );
    }
}
