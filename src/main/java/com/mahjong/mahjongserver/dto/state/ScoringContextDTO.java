package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Set;

public record ScoringContextDTO(
        List<ScoringPatternDTO> scoringPatterns,

        Set<Tile> flowers,
        List<List<Tile>> revealedGroups,
        List<List<Tile>> concealedGroups,

        List<Tile> winningGroup,
        Tile winningTile
) {}