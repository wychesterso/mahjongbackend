package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

public class Turn {
    private Seat seat;
    private Tile drawnTile;
    private Tile takenTile;
    private Tile discardedTile;

    public Turn(Seat seat) {
        this.seat = seat;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setDrawnTile(Tile tile) {
        drawnTile = tile;
    }
}
