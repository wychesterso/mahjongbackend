package com.mahjong.mahjongserver.dto.response;

import com.mahjong.mahjongserver.domain.player.decision.EndGameDecision;

public class EndGameDecisionDTO {
    private String roomId;
    private EndGameDecision decision;

    public EndGameDecisionDTO() {
        // for Spring deserialization
    }

    public EndGameDecisionDTO(String roomId, EndGameDecision decision) {
        this.roomId = roomId;
        this.decision = decision;
    }

    public String getRoomId() {
        return roomId;
    }

    public EndGameDecision getDecision() {
        return decision;
    }
}
