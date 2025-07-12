package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.board.tile.Tile;

import java.util.List;

public class PromptSheungComboDTO {
    private final List<List<Tile>> validSheungs;

    public PromptSheungComboDTO(List<List<Tile>> validSheungs) {
        this.validSheungs = validSheungs;
    }

    public List<List<Tile>> getValidSheungs() {
        return validSheungs;
    }
}