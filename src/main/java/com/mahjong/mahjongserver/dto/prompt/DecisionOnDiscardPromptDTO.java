package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.GameStateDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.List;

public record DecisionOnDiscardPromptDTO(
        GameStateDTO state,
        Tile discardedTile,
        Seat discarder,
        List<Decision> availableOptions,
        List<List<Tile>> sheungCombos
) {}
