package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.Seat;

import java.util.Map;

public record RoomInfoDTO(
            String roomId,
            String hostId,
            int numAvailableSeats,
            Map<Seat, String> playerNames,
            Map<Seat, Boolean> botStatuses
) {}
