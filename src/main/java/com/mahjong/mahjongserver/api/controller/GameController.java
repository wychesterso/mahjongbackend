package com.mahjong.mahjongserver.api.controller;

import com.mahjong.mahjongserver.domain.core.GameService;
import com.mahjong.mahjongserver.dto.response.ClaimResponseDTO;
import com.mahjong.mahjongserver.dto.response.DecisionResponseDTO;
import com.mahjong.mahjongserver.dto.response.DiscardResponseDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles a player's response to a discard prompt.
     * @param response the discard action from the client.
     * @param principal the authenticated player info.
     */
    @MessageMapping("/game/respondDiscard")
    public void handleDiscardResponse(DiscardResponseDTO response, Principal principal) {
        String playerId = principal.getName();
        String roomId = response.getRoomId();
        gameService.handleDiscardResponse(roomId, playerId, response);
    }

    @MessageMapping("/game/respondDecision")
    public void handleDrawDecision(DecisionResponseDTO response, Principal principal) {
        String playerId = principal.getName();
        gameService.handleDrawDecision(response.getRoomId(), playerId, response.getDecision());
    }

    @MessageMapping("/game/respondClaim")
    public void handleClaimDecision(ClaimResponseDTO response, Principal principal) {
        String playerId = principal.getName();
        gameService.handleClaimResponse(response.getRoomId(), playerId, response.getDecision(), response.getSheungCombo());
    }
}
