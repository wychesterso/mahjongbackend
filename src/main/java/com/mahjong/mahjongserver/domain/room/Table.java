package com.mahjong.mahjongserver.domain.room;

import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private Map<Seat, Hand> handMapping = new HashMap<>();
    private Board board = new Board(8);

    public Table() {
        for (Seat seat : Seat.values()) {
            handMapping.put(seat, new Hand());
        }
    }

//============================== GETTERS ==============================//

    public Board getBoard() {
        return board;
    }

    public Hand getHand(Seat seat) {
        return handMapping.get(seat);
    }
}
