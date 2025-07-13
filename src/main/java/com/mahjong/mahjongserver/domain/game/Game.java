package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.dto.table.TableDTO;
import com.mahjong.mahjongserver.dto.mapper.DTOMapper;

public class Game {
    private Table table = new Table();
    private Seat currentSeat;
    private Turn currentTurn;

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

        while (!isGameOver()) {

        }

        handleDraw();
    }

//============================== DRAW TILES ==============================//

    private Tile drawTile(Board board, Hand hand) {
        Tile tile = board.drawTile();
        while (tile.getTileType().getClassification() == TileClassification.FLOWER) {
            hand.addFlower(tile);
            tile = board.drawBonusTile();
        }
        hand.addTile(tile);
        return tile;
    }

    private void dealStartingHands() {
        Board board = table.getBoard();
        for (Seat gameSeat : Seat.values()) {
            Hand hand = table.getHand(gameSeat);
            for (int i = 0; i < 17; i++) {
                drawTile(board, hand);
            }
        }
        drawTile(board, table.getHand(currentSeat));
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


    public void startTurn() {
        currentTurn = new Turn(currentSeat);

        Tile drawnTile = drawTile(table.getBoard(), table.getHand(currentSeat));
        currentTurn.setDrawnTile(drawnTile);

        // Prompt the player to discard
        TableDTO tableDTO = DTOMapper.fromTable(table, currentSeat);
        PlayerContext ctx = room.getPlayerContext(currentSeat);
        ctx.getDecisionHandler().promptDiscardOnDraw(ctx, tableDTO, drawnTile);
    }

    public void startTurnWithoutDraw() {

    }
}
