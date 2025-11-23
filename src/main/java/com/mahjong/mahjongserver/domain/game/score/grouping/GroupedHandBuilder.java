package com.mahjong.mahjongserver.domain.game.score.grouping;

import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.*;

public class GroupedHandBuilder {
    private final Stack<List<Tile>> concealedGroups = new Stack<>();
    private List<Tile> winningGroup = null;
    private MeldType winningGroupType = null;

    public Stack<List<Tile>> getConcealedGroups() {
        return concealedGroups;
    }

    public boolean hasWinningGroup() {
        return winningGroup != null;
    }

    public List<Tile> getWinningGroup() {
        return winningGroup;
    }

    public void setWinningGroup(List<Tile> winningGroup) {
        this.winningGroup = winningGroup;
    }

    public MeldType getWinningGroupType() {
        return winningGroupType;
    }

    public void setWinningGroupType(MeldType winningGroupType) {
        this.winningGroupType = winningGroupType;
    }

    public void addConcealedGroup(List<Tile> group) {
        concealedGroups.push(group);
    }

    public void backtrack() {
        concealedGroups.pop();
    }
}
