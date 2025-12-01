package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.dto.state.GameStateDTO;

public record DiscardPromptDTO(
        GameStateDTO state
) {}
