package com.mahjong.mahjongserver.dto.prompt;

import com.mahjong.mahjongserver.dto.state.TableDTO;

public record DiscardPromptDTO(
        TableDTO table
) {}
