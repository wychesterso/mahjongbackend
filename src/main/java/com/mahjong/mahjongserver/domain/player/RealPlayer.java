package com.mahjong.mahjongserver.domain.player;

public class RealPlayer extends Player {
    public RealPlayer(String id) {
        super(id);
    }

    public boolean isBot() {
        return false;
    }
}
