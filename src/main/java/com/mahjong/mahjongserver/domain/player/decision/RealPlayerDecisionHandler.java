package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.TableDTO;
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
    public void promptDecisionOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile, List<Decision> availableOptions) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_draw_decision",
                Map.of(
                        "table", table,
                        "drawn_tile", drawnTile,
                        "available_options", availableOptions
                )
        );
    }

    @Override
    public void promptDecisionOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder,
                                      List<Decision> availableOptions, List<List<Tile>> sheungCombos) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard_decision",
                Map.of(
                        "table", table,
                        "discarded_tile", discardedTile,
                        "discarder", discarder,
                        "available_options", availableOptions,
                        "sheung_combos", sheungCombos
                )
        );
    }

    @Override
    public void promptDiscard(PlayerContext ctx, TableDTO table) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard",
                Map.of(
                        "table", table
                )
        );
    }

    @Override
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

    @Override
    public void promptEndGameDecision(PlayerContext ctx, Room room) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_end_game_decision",
                null
        );
    }
}
