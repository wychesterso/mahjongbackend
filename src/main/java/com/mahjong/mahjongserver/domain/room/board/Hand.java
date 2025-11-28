package com.mahjong.mahjongserver.domain.room.board;

import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.*;

public class Hand {
    private final List<Tile> concealedTiles = new ArrayList<>();
    private final List<List<Tile>> sheungs = new ArrayList<>();
    private final List<List<Tile>> pongs = new ArrayList<>();
    private final List<List<Tile>> brightKongs = new ArrayList<>();
    private final List<List<Tile>> darkKongs = new ArrayList<>();
    private final Set<Tile> flowers = new HashSet<>();

    public void clearHand() {
        concealedTiles.clear();
        sheungs.clear();
        pongs.clear();
        brightKongs.clear();
        darkKongs.clear();
        flowers.clear();
    }

//============================== GETTERS ==============================//

    public List<Tile> getConcealedTiles() {
        return concealedTiles;
    }

    public Tile getLastDrawnTile() {
        return concealedTiles.getLast();
    }

    public List<List<Tile>> getSheungs() {
        return sheungs;
    }

    public List<List<Tile>> getPongs() {
        return pongs;
    }

    public List<List<Tile>> getBrightKongs() {
        return brightKongs;
    }

    public List<List<Tile>> getDarkKongs() {
        return darkKongs;
    }

    public List<List<Tile>> getRevealedMelds() {
        List<List<Tile>> result = new ArrayList<>();
        result.addAll(sheungs);
        result.addAll(pongs);
        result.addAll(brightKongs);
        result.addAll(darkKongs);
        return result;
    }

    public Set<Tile> getFlowers() {
        return flowers;
    }

//============================== COUNTS ==============================//

    public int getConcealedTileCount() {
        return concealedTiles.size();
    }

    public int getDarkKongCount() {
        return darkKongs.size();
    }

//============================== ADD AND REMOVE TILES ==============================//

    public void addTile(Tile tile) {
        concealedTiles.add(tile);
    }

    public boolean discardTile(Tile tile) {
        return concealedTiles.remove(tile);
    }

    public void addFlower(Tile tile) {
        flowers.add(tile);
    }

//============================== PERFORM HAND OPERATIONS ==============================//

    public boolean performSheung(Tile discardedTile, List<Tile> sheungCombo) {
        for (Tile tile : sheungCombo) {
            if (!concealedTiles.remove(tile)) return false;
        }

        List<Tile> newCombo = new ArrayList<>(sheungCombo);
        newCombo.add(discardedTile);
        Collections.sort(newCombo);
        return sheungs.add(newCombo);
    }

    public boolean performPong(Tile discardedTile) {
        if (!removeFromConcealedHand(discardedTile, 2)) return false;
        return pongs.add(List.of(discardedTile, discardedTile, discardedTile));
    }

    public boolean performBrightKongFromDiscard(Tile discardedTile) {
        if (!removeFromConcealedHand(discardedTile, 3)) return false;
        return brightKongs.add(List.of(discardedTile, discardedTile, discardedTile, discardedTile));
    }

    public boolean performBrightKongFromDraw(Tile targetTile) {
        Iterator<List<Tile>> iterator = pongs.iterator();
        while (iterator.hasNext()) {
            List<Tile> pong = iterator.next();
            if (pong.getFirst() == targetTile) {
                if (!concealedTiles.remove(targetTile)) return false;
                iterator.remove();
                return brightKongs.add(List.of(targetTile, targetTile, targetTile, targetTile));
            }
        }
        return false;
    }

    public boolean performDarkKong(Tile targetTile) {
        if (!removeFromConcealedHand(targetTile, 4)) return false;
        return darkKongs.add(List.of(targetTile, targetTile, targetTile, targetTile));
    }

    private boolean removeFromConcealedHand(Tile targetTile, int count) {
        long actualCount = concealedTiles.stream().filter(t -> t.equals(targetTile)).count();
        if (actualCount < count) return false; // not enough tiles

        // remove 2 matching tiles
        for (int i = 0; i < count; i++) {
            concealedTiles.remove(targetTile);
        }

        return true;
    }
}
