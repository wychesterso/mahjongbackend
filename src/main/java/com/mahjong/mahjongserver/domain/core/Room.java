package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

import java.util.List;

public class Room {
    private final String id;
    private final List<Player> players;
    private final TurnManager turnManager;

    public Room(String id, List<Player> players, GameEventPublisher publisher) {
        this.id = id;
        this.players = players;
        this.turnManager = new TurnManager(players, publisher, id);
    }

    public String getId() {
        return id;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public List<Player> getPlayers() {
        return players;
    }
}