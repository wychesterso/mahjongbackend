package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.core.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotDecisionHandlerFactory {

    private final GameService gameService;

    @Autowired
    public BotDecisionHandlerFactory(GameService gameService) {
        this.gameService = gameService;
    }

    public BotDecisionHandler createBotDecisionHandler() {
        return new BotDecisionHandler(gameService);
    }
}