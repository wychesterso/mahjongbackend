package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Map;

public record TableDTO(
        List<Tile> discardPile,
        int drawPileSize,
        Seat selfSeat,
        Map<Seat, HandDTO> hands
) {}