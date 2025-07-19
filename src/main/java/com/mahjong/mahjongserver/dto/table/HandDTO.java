package com.mahjong.mahjongserver.dto.table;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Set;

public class HandDTO {
    // concealed hand
    private final List<Tile> concealedTiles; // only populate for self's hand; null otherwise
    private final int concealedTileCount;

    // revealed hand
    private final List<List<Tile>> sheungs;
    private final List<List<Tile>> pongs;
    private final List<List<Tile>> brightKongs;
    private final List<List<Tile>> darkKongs; // only populate for self's hand; null otherwise
    private final int darkKongCount;
    private final Set<Tile> flowers;

    public HandDTO(List<Tile> concealedTiles,
                   int concealedTileCount,
                   List<List<Tile>> sheungs,
                   List<List<Tile>> pongs,
                   List<List<Tile>> brightKongs,
                   List<List<Tile>> darkKongs,
                   int darkKongCount,
                   Set<Tile> flowers) {
        this.concealedTiles = concealedTiles;
        this.concealedTileCount = concealedTileCount;
        this.sheungs = sheungs;
        this.pongs = pongs;
        this.brightKongs = brightKongs;
        this.darkKongs = darkKongs;
        this.darkKongCount = darkKongCount;
        this.flowers = flowers;
    }

    public List<Tile> getConcealedTiles() {
        return concealedTiles;
    }

    public int getConcealedTileCount() {
        return concealedTileCount;
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

    public int getDarkKongCount() {
        return darkKongCount;
    }

    public Set<Tile> getFlowers() {
        return flowers;
    }
}