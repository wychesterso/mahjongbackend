package com.mahjong.mahjongserver.domain.player;

public class RealPlayer extends Player {
    private final PlayerProfile profile;

    public RealPlayer(PlayerProfile profile) {
        super(profile.getPlayerId(), 0); // score will be set in Room
        this.profile = profile;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    public PlayerProfile getProfile() {
        return profile;
    }
}
