package com.mahjong.mahjongserver.domain.room.board;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Tile> concealedTiles = new ArrayList<>();
    private final List<List<Tile>> sheungs = new ArrayList<>();
    private final List<List<Tile>> pongs = new ArrayList<>();
    private final List<List<Tile>> brightKongs = new ArrayList<>();
    private final List<List<Tile>> darkKongs = new ArrayList<>();
    private final List<Tile> flowers = new ArrayList<>();

//============================== GETTERS ==============================//

    public List<Tile> getConcealedTiles() {
        return concealedTiles;
    }

    public List<List<Tile>> getSheungs() {
        return sheungs;
    }

    public List<List<Tile>> getPongs() {
        return pongs;
    }

    public List<List<Tile>> getBrightKongs() {
        return brightKongs;
    }

    public List<List<Tile>> getDarkKongs() {
        return darkKongs;
    }

    public List<Tile> getFlowers() {
        return flowers;
    }

//============================== ADD AND REMOVE TILES ==============================//

    public void addTile(Tile tile) {
        concealedTiles.add(tile);
    }

    public boolean discardTile(Tile tile) {
        return concealedTiles.remove(tile);
    }

    public void addFlower(Tile tile) {
        flowers.add(tile);
    }

//============================== PERFORM HAND OPERATIONS ==============================//

    public boolean performSheung(List<Tile> tiles) {
        if (concealedTiles.remove(tiles.get(0))) {
            if (concealedTiles.remove(tiles.get(1))) {
                if (concealedTiles.remove(tiles.get(2))) {
                    return sheungs.add(tiles);
                }
                concealedTiles.add(tiles.get(1));
            }
            concealedTiles.add(tiles.get(0));
        }
        return false;
    }

    public boolean performBrightPong(Tile tile) {
        for (int i = 0; i < 2; i++) {
            if (!concealedTiles.remove(tile)) return false;
        }
        return pongs.add(List.of(tile, tile, tile));
    }

    public boolean performDarkPong(Tile tile) {
        for (int i = 0; i < 3; i++) {
            if (!concealedTiles.remove(tile)) return false;
        }
        return pongs.add(List.of(tile, tile, tile));
    }

    public boolean performBrightKong(Tile tile) {
        for (List<Tile> pong : pongs) {
            if (pong.getFirst() == tile) {
                pongs.remove(pong);
                return brightKongs.add(List.of(tile, tile, tile, tile));
            }
        }
        return false;
    }

    public boolean performDarkKong(Tile tile) {
        for (int i = 0; i < 4; i++) {
            if (!concealedTiles.remove(tile)) return false;
        }
        return darkKongs.add(List.of(tile, tile, tile, tile));
    }
}
