package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.GameStateDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.List;

public record DecisionOnDrawPromptDTO(
        GameStateDTO state,
        Tile drawnTile,
        List<Decision> availableOptions,
        List<Tile> availableBrightKongs,
        List<Tile> availableDarkKongs
) {}
