package com.mahjong.mahjongserver.service;

import com.mahjong.mahjongserver.domain.player.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {
    private final Map<String, Player> players = new HashMap<>();

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public void addPlayer(Player player) {
        players.put(player.getUsername(), player);
    }

    public void removePlayer(String name) {
        players.remove(name);
    }

    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(players.get(name));
    }

    public void updatePlayer(String name, Player updated) {
        players.put(name, updated);
    }

    public void loadPlayers() {
        // read from file
    }

    public void savePlayers() {
        // write to file
    }
}