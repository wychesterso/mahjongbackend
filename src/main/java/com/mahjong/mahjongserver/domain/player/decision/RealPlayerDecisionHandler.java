package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.prompt.DiscardAfterDrawPromptDTO;
import com.mahjong.mahjongserver.dto.prompt.DecisionOnDiscardPromptDTO;
import com.mahjong.mahjongserver.dto.prompt.DecisionOnDrawPromptDTO;
import com.mahjong.mahjongserver.dto.prompt.DiscardPromptDTO;
import com.mahjong.mahjongserver.dto.state.GameStateDTO;
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
    public void promptDecisionOnDraw(PlayerContext ctx, GameStateDTO state, Tile drawnTile,
                                     List<Decision> availableOptions, List<Tile> availableBrightKongs,
                                     List<Tile> availableDarkKongs) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_draw_decision",
                new DecisionOnDrawPromptDTO(state, drawnTile, availableOptions, availableBrightKongs, availableDarkKongs)
        );
    }

    @Override
    public void promptDecisionOnDiscard(PlayerContext ctx, GameStateDTO state, Tile discardedTile, Seat discarder,
                                        List<Decision> availableOptions, List<List<Tile>> sheungCombos) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard_decision",
                new DecisionOnDiscardPromptDTO(state, discardedTile, discarder, availableOptions, sheungCombos)
        );
    }

    @Override
    public void promptDiscard(PlayerContext ctx, GameStateDTO state) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard",
                new DiscardPromptDTO(state)
        );
    }

    @Override
    public void promptDiscardOnDraw(PlayerContext ctx, GameStateDTO state, Tile drawnTile) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_discard_on_draw",
                new DiscardAfterDrawPromptDTO(state, drawnTile)
        );
    }

    @Override
    public void promptEndGameDecision(PlayerContext ctx, Room room) {
        publisher.sendPrompt(
                ctx.getPlayer().getId(),
                "prompt_end_game_decision",
                Map.of()
        );
    }
}
