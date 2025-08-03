package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Set;

public record HandDTO(List<Tile> concealedTiles,
                   int concealedTileCount,
                   List<List<Tile>> sheungs,
                   List<List<Tile>> pongs,
                   List<List<Tile>> brightKongs,
                   List<List<Tile>> darkKongs, // only populate for self's hand; null otherwise
                   int darkKongCount,
                   Set<Tile> flowers) {}