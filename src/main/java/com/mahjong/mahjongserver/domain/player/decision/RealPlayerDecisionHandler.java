package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.dto.*;
import com.mahjong.mahjongserver.game.PlayerContext;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RealPlayerDecisionHandler implements PlayerDecisionHandler {
    private final GameEventPublisher publisher;

    public RealPlayerDecisionHandler(GameEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void promptWin(PlayerContext ctx, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_win",
                new PromptMoveDTO(
                        boardState,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptWin(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_win_self_draw",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptSheung(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_sheung",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptPong(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_pong",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptDarkKong(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_dark_kong",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptBrightKong(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_bright_kong",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptBrightKongNoDraw(PlayerContext ctx, Tile tile, BoardStateDTO boardState) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_bright_kong_no_draw",
                new PromptMoveGivenTileDTO(
                        boardState,
                        tile,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand())
                )
        );
    }

    @Override
    public void promptSheungCombo(PlayerContext ctx, List<List<Tile>> validSheungs) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_sheung_combo",
                new PromptSheungComboDTO(validSheungs)
        );
    }

    @Override
    public void promptDiscard(PlayerContext ctx, BoardStateDTO boardState, List<Tile> discardedTiles) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_discard",
                new PromptDiscardDTO(
                        boardState,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand()),
                        false
                )
        );
    }

    @Override
    public void promptDiscardNoDraw(PlayerContext ctx, BoardStateDTO boardState, List<Tile> discardedTiles) {
        publisher.sendPrompt(
                ctx.getPlayerId(),
                "prompt_discard_no_draw",
                new PromptDiscardDTO(
                        boardState,
                        new HandDTO(ctx.getPlayer().getHandManager().getHand()),
                        new RevealedHandDTO(ctx.getPlayer().getHandManager().getRevealedHand()),
                        true
                )
        );
    }
}
