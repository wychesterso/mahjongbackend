package com.mahjong.mahjongserver.dto.table;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Map;

public class TableDTO {
    private final List<Tile> discardPile;
    private final int drawPileSize;
    private final Seat selfSeat;
    private final Map<Seat, HandDTO> hands;

    public TableDTO(List<Tile> discardPile,
                    int drawPileSize,
                    Seat selfSeat,
                    Map<Seat, HandDTO> hands) {
        this.discardPile = discardPile;
        this.drawPileSize = drawPileSize;
        this.selfSeat = selfSeat;
        this.hands = hands;
    }

    public List<Tile> getDiscardPile() {
        return discardPile;
    }

    public int getDrawPileSize() {
        return drawPileSize;
    }

    public Seat getSelfSeat() {
        return selfSeat;
    }

    public Map<Seat, HandDTO> getHands() {
        return hands;
    }
}
