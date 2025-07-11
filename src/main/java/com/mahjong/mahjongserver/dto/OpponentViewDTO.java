package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.tile.Tile;

import java.util.List;

public class OpponentViewDTO {
    private final int concealedTileCount;
    private final List<List<Tile>> groups;
    private final List<List<Tile>> brightKongs;
    private final List<List<Tile>> darkKongs;
    private final List<Tile> flowers;

    public OpponentViewDTO(int concealedTileCount, List<List<Tile>> groups, List<List<Tile>> brightKongs,
                           List<List<Tile>> darkKongs, List<Tile> flowers) {
        this.concealedTileCount = concealedTileCount;
        this.groups = groups;
        this.brightKongs = brightKongs;
        this.darkKongs = darkKongs;
        this.flowers = flowers;
    }

    public int getConcealedTileCount() {
        return concealedTileCount;
    }

    public List<List<Tile>> getGroups() {
        return groups;
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
}