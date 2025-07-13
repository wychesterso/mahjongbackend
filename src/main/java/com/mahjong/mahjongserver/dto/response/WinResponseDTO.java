package com.mahjong.mahjongserver.dto.response;

public class WinResponseDTO {
    private String roomId;
    private boolean decision;

    public WinResponseDTO() {
        // for Spring deserialization
    }

    public WinResponseDTO(String roomId, boolean decision) {
        this.roomId = roomId;
        this.decision = decision;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean getDecision() {
        return decision;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
    }
}
