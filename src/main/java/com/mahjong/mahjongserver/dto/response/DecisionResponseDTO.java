package com.mahjong.mahjongserver.dto.response;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

public class DecisionResponseDTO {
    private String roomId;
    private Decision decision;
    private Tile kongTile;

    public DecisionResponseDTO() {
        // for Spring deserialization
    }

    public DecisionResponseDTO(String roomId, Decision decision, Tile kongTile) {
        this.roomId = roomId;
        this.decision = decision;
        this.kongTile = kongTile;
    }

    public String getRoomId() {
        return roomId;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Tile getKongTile() {
        return kongTile;
    }

    public void setKongTile(Tile kongTile) {
        this.kongTile = kongTile;
    }
}
