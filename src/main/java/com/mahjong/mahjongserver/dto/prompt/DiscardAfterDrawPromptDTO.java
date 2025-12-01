package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.GameStateDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

public record DiscardAfterDrawPromptDTO(
        GameStateDTO state,
        Tile drawnTile
) {}
