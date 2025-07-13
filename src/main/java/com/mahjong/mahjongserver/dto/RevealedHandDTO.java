package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class RevealedHandDTO {
    private final List<List<Tile>> sheungs;
    private final List<List<Tile>> pongs;
    private final List<List<Tile>> brightKongs;
    private final List<List<Tile>> darkKongs; // only populate for self
    private final int darkKongCount;
    private final List<Tile> flowers;

    public RevealedHandDTO(List<List<Tile>> sheungs,
                           List<List<Tile>> pongs,
                           List<List<Tile>> brightKongs,
                           List<List<Tile>> darkKongs,
                           int darkKongCount,
                           List<Tile> flowers) {
        this.sheungs = sheungs;
        this.pongs = pongs;
        this.brightKongs = brightKongs;
        this.darkKongs = darkKongs;
        this.darkKongCount = darkKongCount;
        this.flowers = flowers;
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

    public List<Tile> getFlowers() {
        return flowers;
    }
}