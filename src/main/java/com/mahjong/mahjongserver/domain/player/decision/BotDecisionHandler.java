package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.core.GameService;
import com.mahjong.mahjongserver.domain.game.score.HandGrouper;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.response.DiscardResponseDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.ArrayList;
import java.util.List;

public class BotDecisionHandler implements PlayerDecisionHandler {

    private final GameService gameService;

    public BotDecisionHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void promptDecisionOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile, List<Decision> availableOptions) {
        Decision decision = Decision.PASS;

        if (availableOptions.contains(Decision.WIN) && decideWin(ctx, table, drawnTile, table.selfSeat())) {
            decision = Decision.WIN;
        } else if (availableOptions.contains(Decision.DARK_KONG) && decideKong(ctx, table, drawnTile, table.selfSeat())) {
            decision = Decision.DARK_KONG;
        } else if (availableOptions.contains(Decision.BRIGHT_KONG) && decideKong(ctx, table, drawnTile, table.selfSeat())) {
            decision = Decision.BRIGHT_KONG;
        }

        gameService.handleDrawClaim(ctx.getRoomId(), ctx.getPlayer().getId(), decision);
    }

    @Override
    public void promptDecisionOnDiscard(PlayerContext ctx, TableDTO table, Tile discardedTile, Seat discarder,
                                        List<Decision> availableOptions, List<List<Tile>> sheungCombos) {
        Decision decision = Decision.PASS;
        List<Tile> sheungCombo = null;

        if (availableOptions.contains(Decision.WIN) && decideWin(ctx, table, discardedTile, discarder)) {
            decision = Decision.WIN;
        } else if (availableOptions.contains(Decision.DARK_KONG) && decideKong(ctx, table, discardedTile, discarder)) {
            decision = Decision.DARK_KONG;
        } else if (availableOptions.contains(Decision.BRIGHT_KONG) && decideKong(ctx, table, discardedTile, discarder)) {
            decision = Decision.BRIGHT_KONG;
        } else if (availableOptions.contains(Decision.PONG) && decideKong(ctx, table, discardedTile, discarder)) {
            decision = Decision.PONG;
        } else if (availableOptions.contains(Decision.SHEUNG) && decideKong(ctx, table, discardedTile, discarder)) {
            decision = Decision.SHEUNG;
            sheungCombo = decideSheungCombo(ctx, table, sheungCombos);
        }

        gameService.handleDiscardClaim(ctx.getRoomId(), ctx.getPlayer().getId(), decision, sheungCombo);
    }

    @Override
    public void promptDiscard(PlayerContext ctx, TableDTO table) {
        List<Tile> concealedTiles = table.hands().get(table.selfSeat()).concealedTiles();
        Tile tileToDiscard = HandGrouper.getTilesToDiscard(concealedTiles, table.discardPile()).getFirst();

        DiscardResponseDTO response = new DiscardResponseDTO();
        response.setRoomId(ctx.getRoomId());
        response.setTile(tileToDiscard);

        gameService.handleDiscardResponse(ctx.getRoomId(), ctx.getPlayer().getId(), response);
    }

    @Override
    public void promptDiscardOnDraw(PlayerContext ctx, TableDTO table, Tile drawnTile) {
        // we don't care which tile was drawn
        promptDiscard(ctx, table);
    }

    @Override
    public void promptEndGameDecision(PlayerContext ctx, Room room) {
        room.collectEndGameDecision(ctx.getPlayer(), EndGameDecision.NEXT_GAME);
    }

//============================== HELPER DECISION-MAKING ==============================//

    private boolean decideWin(PlayerContext ctx,
                              TableDTO table,
                              Tile tile,
                              Seat discarder) {
        // bot always chooses to win
        return true;
    }

    private boolean decideKong(PlayerContext ctx,
                               TableDTO table,
                               Tile tile,
                               Seat discarder) {
        List<Tile> concealedTiles = new ArrayList<>(table.hands().get(table.selfSeat()).concealedTiles());
        List<Tile> groupedTiles = new ArrayList<>();
        HandGrouper.groupThreeConsecutiveTiles(concealedTiles, groupedTiles);

        // only kong when it's not used in a sheung
        return !groupedTiles.contains(tile);
    }

    private boolean decidePong(PlayerContext ctx,
                               TableDTO table,
                               Tile tile,
                               Seat discarder) {
        return true;
    }

    private boolean decideSheung(PlayerContext ctx,
                                 TableDTO table,
                                 Tile tile,
                                 Seat discarder) {
        // same logic for now - only sheung when it's not used in another sheung
        // TODO: implement more advanced logic, check for pongs, discarded tiles and other players' hands
        return decideKong(ctx, table, tile, discarder);
    }

    private List<Tile> decideSheungCombo(PlayerContext ctx,
                                         TableDTO table,
                                         List<List<Tile>> sheungCombos) {
        // TODO: implement more advanced logic for picking sheung (e.g. prioritizing edges that use 1,2,8,9)
        return sheungCombos.getFirst();
    }
}
