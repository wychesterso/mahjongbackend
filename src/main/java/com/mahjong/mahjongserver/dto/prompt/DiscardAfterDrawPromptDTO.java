package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.TableDTO;

public record DiscardAfterDrawPromptDTO(
        TableDTO table,
        Tile drawnTile
) {}
