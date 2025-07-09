package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.domain.board.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public final class TileHelper {
    private TileHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Checks if the given tiles are of the same type.
     * @param tiles the tiles to check.
     * @return true iff the given tiles are of the same type, false otherwise.
     */
    public static boolean checkSameType(List<Tile> tiles) {
        TileType type = tiles.getFirst().getTileType();
        for (Tile tile : tiles) {
            if (tile.getTileType() != type) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all given tiles are of different types.
     * @param tiles the tiles to check.
     * @return true iff all given tiles are of different types, false otherwise.
     */
    public static boolean checkDifferentType(List<Tile> tiles) {
        if (tiles.size() > 3) {
            return false;
        }
        List<TileType> existingTypes = new ArrayList<>();
        for (Tile tile : tiles) {
            TileType type = tile.getTileType();
            if (existingTypes.contains(type)) {
                return false;
            }
            existingTypes.add(type);
        }
        return true;
    }

    /**
     * Checks if the given tiles have the same tile number.
     * @param tiles the tiles to check.
     * @return true iff the given tiles have the same tile number, false otherwise.
     */
    public static boolean checkSameNum(List<Tile> tiles) {
        int tileNum = tiles.getFirst().getTileNum();
        List<TileType> validTypes = List.of(TileType.TUNG, TileType.SOK, TileType.MAAN);
        for (Tile tile : tiles) {
            if (!validTypes.contains(tile.getTileType()) || tile.getTileNum() != tileNum) {
                return false;
            }
        }
        return true;
    }
}
