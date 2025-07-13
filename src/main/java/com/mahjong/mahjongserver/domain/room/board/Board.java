package com.mahjong.mahjongserver.domain.room.board;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;

import java.util.*;

public class Board {
    private int minTilesLeft;
    private List<Tile> drawPile = new ArrayList<>();
    private Stack<Tile> discardPile = new Stack<>();
    private boolean canTakeDiscard = false;

    public Board(int minTilesLeft) {
        this.minTilesLeft = minTilesLeft;
        for (Tile tile : Tile.values()) {
            if (tile.getTileType().getClassification() == TileClassification.FLOWER) {
                drawPile.add(tile);
            } else {
                for (int i = 0; i < 4; i++) {
                    drawPile.add(tile);
                }
            }
        }
        Collections.shuffle(drawPile);
    }

//============================== GETTERS ==============================//

    public int getMinTilesLeft() {
        return minTilesLeft;
    }

    public List<Tile> getDrawPile() {
        return drawPile;
    }

    public Stack<Tile> getDiscardPile() {
        return discardPile;
    }

    public boolean canTakeDiscard() {
        return canTakeDiscard;
    }

//============================== TILE COUNTS ==============================//

    public int getDrawPileSize() {
        return drawPile.size();
    }

    public int getDiscardPileSize() {
        return discardPile.size();
    }

//============================== DRAW AND DISCARD TILES ==============================//

    public Tile takeFromDrawPile() {
        return drawPile.getFirst();
    }

    public void putInDiscardPile(Tile tile) {
        discardPile.add(tile);
        canTakeDiscard = true;
    }

    public Tile getLastDiscardedTile() {
        return discardPile.peek();
    }

    public Tile takeFromDiscardPile() {
        if (!canTakeDiscard) return null;
        canTakeDiscard = false;
        return discardPile.pop();
    }
}
