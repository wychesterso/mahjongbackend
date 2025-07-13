package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

public class Game {
    private Table table = new Table();
    private Seat currentSeat;

    private GameEventPublisher gameEventPublisher;
    private String roomId;

    public Game(GameEventPublisher gameEventPublisher, String roomId) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
        startGame(Seat.EAST);
    }

    public Game(GameEventPublisher gameEventPublisher, String roomId, Seat seat) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
        startGame(seat);
    }

    private void startGame(Seat seat) {
        currentSeat = seat;

        // populate hands
        Board board = table.getBoard();
        for (Seat gameSeat : Seat.values()) {
            Hand hand = table.getHand(gameSeat);
            for (int i = 0; i < 17; i++) {
                drawTile(board, hand);
            }
        }
        drawTile(board, table.getHand(seat));
    }

    private void drawTile(Board board, Hand hand) {
        Tile tile = board.takeFromDrawPile();
        while (tile.getTileType().getClassification() == TileClassification.FLOWER) {
            hand.addFlower(tile);
            tile = board.takeFromDrawPile();
        }
        hand.addTile(tile);
    }

    public void startTurn() {
        Hand hand = table.getHand(currentSeat);

    }

    public void startTurnWithoutDraw() {

    }
}
