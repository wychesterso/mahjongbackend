package com.mahjong.mahjongserver.dto.response;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class ClaimResponseDTO {
    private String roomId;
    private Decision decision;
    private List<Tile> sheungCombo;

    public ClaimResponseDTO() {
        // for Spring deserialization
    }

    public ClaimResponseDTO(String roomId, Decision decision, List<Tile> sheungCombo) {
        this.roomId = roomId;
        this.decision = decision;
        this.sheungCombo = sheungCombo;
    }

    public String getRoomId() {
        return roomId;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<Tile> getSheungCombo() {
        return sheungCombo;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public void setSheungCombo(List<Tile> sheungCombo) {
        this.sheungCombo = sheungCombo;
    }
}
