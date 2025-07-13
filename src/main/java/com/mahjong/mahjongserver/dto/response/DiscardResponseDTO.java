package com.mahjong.mahjongserver.dto.response;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

public class DiscardResponseDTO {
    private String roomId;

    /** The tile that the player chose to discard. */
    private Tile tile;

    public DiscardResponseDTO() {
        // for Spring deserialization
    }

    public DiscardResponseDTO(String roomId, Tile tile) {
        this.roomId = roomId;
        this.tile = tile;
    }

    public String getRoomId() {
        return roomId;
    }

    public Tile getTile() {
        return tile;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}