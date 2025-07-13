package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.dto.response.DiscardResponseDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameController {
    @MessageMapping("/game/respondDiscard")
    public void handleDiscardResponse(DiscardResponseDTO response, Principal principal) {
//        String playerId = principal.getName();
//        String roomId = response.getRoomId(); // included in every message
//
//        gameManager.handlePlayerResponse(roomId, playerId, response);
    }
}
