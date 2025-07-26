package com.mahjong.mahjongserver.service;

import com.mahjong.mahjongserver.domain.player.PlayerProfile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayerProfileService {
    private final Map<String, PlayerProfile> storage = new ConcurrentHashMap<>();

    public PlayerProfile loadProfile(String playerId) {
        return storage.computeIfAbsent(playerId, id -> new PlayerProfile(id, 1000));
    }

    public void saveProfile(PlayerProfile profile) {
        storage.put(profile.getPlayerId(), profile);
    }
}