package com.mahjong.mahjongserver.dto.response;

import com.mahjong.mahjongserver.domain.player.decision.Decision;

public class DecisionResponseDTO {
    private String roomId;
    private Decision decision;

    public DecisionResponseDTO() {
        // for Spring deserialization
    }

    public DecisionResponseDTO(String roomId, Decision decision) {
        this.roomId = roomId;
        this.decision = decision;
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
}
