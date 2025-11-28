package com.mahjong.mahjongserver.domain.game.score.data;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;

public class Meld {
    private final MeldType meldType;
    private final Tile startingTile;
    private final List<Tile> group;
    private final boolean isRevealed;

    public Meld(MeldType meldType, Tile startingTile, List<Tile> group, boolean isRevealed) {
        this.meldType = meldType;
        this.startingTile = startingTile;
        this.group = group;
        this.isRevealed = isRevealed;
    }

    public MeldType getMeldType() {
        return meldType;
    }

    public Tile getStartingTile() {
        return startingTile;
    }

    public List<Tile> getGroup() {
        return group;
    }

    public boolean isRevealed() {
        return isRevealed;
    }
}
