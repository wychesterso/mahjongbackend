package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.room.Seat;

import java.util.Map;
import java.util.Set;

public record EndGameDTO(
    Map<Seat, ScoringContextDTO> winners,
    Set<Seat> loserSeats,
    TableDTO tableDTO
) {}
