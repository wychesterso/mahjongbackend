package com.mahjong.mahjongserver.domain.player.decision;

import com.mahjong.mahjongserver.domain.core.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class BotDecisionHandlerFactory {

    private GameService gameService;

    @Autowired
    public void setGameService(@Lazy GameService gameService) {
        this.gameService = gameService;
    }

    public BotDecisionHandler createBotDecisionHandler() {
        return new BotDecisionHandler(gameService);
    }
}