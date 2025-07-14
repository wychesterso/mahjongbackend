package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.dto.mapper.DTOMapper;
import com.mahjong.mahjongserver.dto.table.TableDTO;

import java.util.List;

public class Game {
    private final Table table = new Table();
    private Seat currentSeat;

    private final Room room;

    public Game(Room room) {
        this.room = room;
        currentSeat = Seat.EAST;
    }

    public Game(Room room, Seat seat) {
        this.room = room;
        currentSeat = seat;
    }

//============================== GETTERS ==============================//

    public Table getTable() {
        return table;
    }

    public Board getBoard() {
        return table.getBoard();
    }

    public Seat getCurrentSeat() {
        return currentSeat;
    }

//============================== ACCESS POINT - FLOW OF ONE MAHJONG GAME ==============================//

    public void runGame() {
        dealStartingHands();
        boolean drawTileOnTurnStart = false;

        // check for win / dark kong

        while (!isGameOver()) {
            Hand currentHand = table.getHand(currentSeat);

            if (drawTileOnTurnStart) {
                Tile drawnTile = drawTile(currentHand);

                // check for win / dark kong / bright kong
                // if found, prompt decision and break/continue loop or pass accordingly

                // prompt a discard
                promptDiscardOnDraw(drawnTile);

            } else {
                // prompt a discard
                promptDiscard();
            }

            // check other players' hands for win / pong / sheung
            // if found, prompt decision and break/continue loop or pass accordingly

            // if no one picks up discarded tile, move to next player
            currentSeat = currentSeat.next();
            drawTileOnTurnStart = true;
        }

        handleDraw();
    }

//============================== DRAW TILES ==============================//

    private Tile drawTile(Hand hand) {
        Tile tile = getBoard().drawTile();
        while (tile.getTileType().getClassification() == TileClassification.FLOWER) {
            hand.addFlower(tile);
            tile = getBoard().drawBonusTile();
        }
        hand.addTile(tile);
        updateTableState();
        return tile;
    }

    private void dealStartingHands() {
        for (Seat gameSeat : Seat.values()) {
            Hand hand = table.getHand(gameSeat);
            for (int i = 0; i < 16; i++) {
                drawTile(hand);
            }
        }
        drawTile(table.getHand(currentSeat));
    }

//============================== GAME OVER ==============================//

    private boolean isGameOver() {
        return false;
    }

    private void handleWin() {

    }

    private void handleDraw() {

    }

//============================== PROMPT FRONTEND ==============================//

    private void promptDecision(Tile discardedTile, Seat discarder, List<Decision> availableOptions) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDecision(ctx, fromTable(), discardedTile, discarder, availableOptions);
    }

    private void promptSheungCombo(Tile discardedTile, List<List<Tile>> validCombos) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptSheungCombo(ctx, discardedTile, validCombos);
    }

    private void promptDiscard() {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscard(ctx, fromTable());
    }

    private void promptDiscardOnDraw(Tile drawnTile) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscardOnDraw(ctx, fromTable(), drawnTile);
    }

//============================== UPDATE FRONTEND ==============================//

    /**
     * Sends a personalized update of the current game table state to all players.
     */
    private void updateTableState() {
        for (Seat seat : Seat.values()) {
            PlayerContext ctx = room.getPlayerContext(seat);
            room.getGameEventPublisher().sendTableUpdate(
                    ctx.getPlayer().getId(),
                    DTOMapper.fromTable(table, seat)
            );
        }
    }

//============================== HELPERS ==============================//

    private PlayerContext getPlayerContext() {
        return room.getPlayerContext(currentSeat);
    }

    private TableDTO fromTable() {
        return DTOMapper.fromTable(table, currentSeat);
    }
}
