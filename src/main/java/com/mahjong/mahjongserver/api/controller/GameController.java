package com.mahjong.mahjongserver.api.controller;

import com.mahjong.mahjongserver.domain.core.GameService;
import com.mahjong.mahjongserver.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Starts a game in an active room with full seats.
     * @param roomId the id of the room to start a game in.
     * @param principal the authenticated player info.
     * @return
     */
    @PostMapping("/rooms/{roomId}/start")
    public ResponseEntity<Void> startGame(
            @PathVariable String roomId,
            Principal principal) {
        String playerId = principal.getName();

        gameService.startGame(roomId, playerId);
        return ResponseEntity.ok().build();
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

    @MessageMapping("/game/respondDrawClaim")
    public void handleDrawDecision(DecisionResponseDTO response, Principal principal) {
        String playerId = principal.getName();
        gameService.handleDrawClaim(response.getRoomId(), playerId, response.getDecision());
    }

    @MessageMapping("/game/respondDiscardClaim")
    public void handleClaimDecision(ClaimResponseDTO response, Principal principal) {
        String playerId = principal.getName();
        gameService.handleDiscardClaim(response.getRoomId(), playerId, response.getDecision(), response.getSheungCombo());
    }

    @MessageMapping("/game/endGameDecision")
    public void handleEndGameDecision(EndGameDecisionDTO response, Principal principal) {
        String playerId = principal.getName();
        gameService.handleEndGameDecision(response.getRoomId(), playerId, response.getDecision());
    }
}
