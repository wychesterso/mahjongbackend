package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.Seat;

import java.util.Map;

public class RoomInfoDTO {
    private String roomId;
    private String hostId;
    private int numAvailableSeats;
    private Map<Seat, String> playerNames;

    public RoomInfoDTO(String roomId, String hostId, int numAvailableSeats, Map<Seat, String> playerNames) {
        this.roomId = roomId;
        this.hostId = hostId;
        this.numAvailableSeats = numAvailableSeats;
        this.playerNames = playerNames;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getHostId() {
        return hostId;
    }

    public int getNumAvailableSeats() {
        return numAvailableSeats;
    }

    public Map<Seat, String> getPlayerNames() {
        return playerNames;
    }
}
