package com.mahjong.mahjongserver.dto.response;

import java.util.List;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

public class SheungComboResponseDTO {
    private String roomId;
    private List<Tile> selectedCombo;

    public SheungComboResponseDTO() {
        // for Spring deserialization
    }

    public SheungComboResponseDTO(String roomId, List<Tile> selectedCombo) {
        this.roomId = roomId;
        this.selectedCombo = selectedCombo;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<Tile> getSelectedCombo() {
        return selectedCombo;
    }

    public void setSelectedCombo(List<Tile> selectedCombo) {
        this.selectedCombo = selectedCombo;
    }
}