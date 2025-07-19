package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public record TileGroupDTO(
        String type,
        List<Tile> group
) {}