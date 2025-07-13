package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

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

//============================== ACCESS POINT - FLOW OF ONE MAHJONG GAME ==============================//

    public void runGame() {
        dealStartingHands();
        boolean drawTileOnTurnStart = false;

        while (!isGameOver()) {
            Hand currentHand = table.getHand(currentSeat);

            if (drawTileOnTurnStart) {
                drawTile(currentHand);

                // check for win / dark kong / bright kong
                // if found, prompt decision and break/continue loop or pass accordingly

                // prompt a discard

            } else {
                // prompt a discard
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

//============================== TURNS ==============================//

//============================== GAME OVER ==============================//

    private boolean isGameOver() {
        return false;
    }

    private void handleWin() {

    }

    private void handleDraw() {

    }





    public Board getBoard() {
        return table.getBoard();
    }

//
//    public void startTurn() {
//        currentTurn = new Turn(currentSeat);
//
//        Tile drawnTile = drawTile(table.getBoard(), table.getHand(currentSeat));
//        currentTurn.setDrawnTile(drawnTile);
//
//        // Prompt the player to discard
//        TableDTO tableDTO = DTOMapper.fromTable(table, currentSeat);
//        PlayerContext ctx = room.getPlayerContext(currentSeat);
//        ctx.getDecisionHandler().promptDiscardOnDraw(ctx, tableDTO, drawnTile);
//    }
//
//    public void startTurnWithoutDraw() {
//
//    }
}
