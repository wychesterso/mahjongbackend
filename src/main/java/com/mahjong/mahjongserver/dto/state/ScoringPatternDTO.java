package com.mahjong.mahjongserver.dto.state;

public record ScoringPatternDTO(
        String id,
        String name,
        String description,
        int value
) {}
