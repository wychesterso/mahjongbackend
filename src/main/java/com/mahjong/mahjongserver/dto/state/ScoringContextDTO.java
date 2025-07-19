package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

import java.util.List;

public record ScoringContextDTO(
        List<ScoringPattern> scoringPatterns,
        List<TileGroupDTO> groups
) {}