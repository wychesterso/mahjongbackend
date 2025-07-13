package com.mahjong.mahjongserver.domain.room;

public enum Seat {
    EAST,
    SOUTH,
    WEST,
    NORTH,
    ;

    public Seat next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
