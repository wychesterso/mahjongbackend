package com.mahjong.mahjongserver.domain.player;

public class Bot extends Player {
    public Bot(String id) {
        super(id);
    }

    public boolean isBot() {
        return true;
    }
}