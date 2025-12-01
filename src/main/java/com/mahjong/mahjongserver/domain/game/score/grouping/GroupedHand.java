package com.mahjong.mahjongserver.domain.game.score.grouping;

import com.mahjong.mahjongserver.domain.game.score.data.Meld;
import com.mahjong.mahjongserver.domain.game.score.data.MeldType;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupedHand {

    // =============== MELDS ===============
    private final Set<Tile> flowers = new HashSet<>();
    private final Map<MeldType, List<Meld>> meldsByMeldType = new HashMap<>();
    private final Map<TileType, List<Meld>> meldsByTileType = new HashMap<>();

    // =============== WINNING GROUP ===============
    private final Meld winningMeld;

    // =============== CONSTRUCTOR ===============
    public GroupedHand(GroupedHandBuilder builder, Hand hand, boolean isSelfDraw) {
        if (!builder.hasWinningGroup()) throw new IllegalArgumentException("[GroupedHand] builder has no winning group!");
        initializeListsInMaps();

        List<Tile> winningGroupTiles = builder.getWinningGroup();
        winningMeld = addMeldKnownType(winningGroupTiles, builder.getWinningGroupType(), !isSelfDraw);

        flowers.addAll(hand.getFlowers());

        for (List<Tile> group : builder.getConcealedGroups()) {
            addMeld(group, false);
        }
        for (List<Tile> group : hand.getRevealedMelds()) {
            addMeld(group, true);
        }
    }

    private void initializeListsInMaps() {
        for (MeldType meldType : MeldType.values()) {
            meldsByMeldType.put(meldType, new ArrayList<>());
        }
        for (TileType tileType : List.of(TileType.CIRCLE, TileType.BAMBOO, TileType.MILLION, TileType.WIND, TileType.DRAGON)) {
            meldsByTileType.put(tileType, new ArrayList<>());
        }
    }

    private void addMeld(List<Tile> group, boolean isRevealed) {
        switch (group.size()) {
            case 2 -> {addMeldKnownType(group, MeldType.PAIR, isRevealed);}
            case 3 -> {
                if (group.getFirst() == group.getLast()) {
                    addMeldKnownType(group, MeldType.PONG, isRevealed);
                } else {
                    addMeldKnownType(group, MeldType.SHEUNG, isRevealed);
                }
            }
            case 4 -> {addMeldKnownType(group, MeldType.KONG, isRevealed);}
            case 12 -> {addMeldKnownType(group, MeldType.THIRTEEN_ORPHANS, isRevealed);}
            case 15 -> {addMeldKnownType(group, MeldType.SIXTEEN_DISJOINT, isRevealed);}
            default -> {addMeldKnownType(group, MeldType.OTHER, isRevealed);}
        }
    }

    private Meld addMeldKnownType(List<Tile> group, MeldType meldType, boolean isRevealed) {
        Tile startingTile = group.getFirst();
        Meld meld = new Meld(meldType, startingTile, group, isRevealed);

        meldsByMeldType.get(meldType).add(meld);
        meldsByTileType.get(startingTile.getTileType()).add(meld);

        return meld;
    }

    // =============== GROUP GETTERS ===============

    public Meld getWinningMeld() {
        return winningMeld;
    }

    private List<Meld> getConcealed(MeldType type) {
        return meldsByMeldType.get(type)
                .stream()
                .filter(m -> !m.isRevealed())
                .toList();
    }

    private List<Meld> getRevealed(MeldType type) {
        return meldsByMeldType.get(type)
                .stream()
                .filter(Meld::isRevealed)
                .toList();
    }

    // =============== FLOWERS ===============

    public Set<Tile> getFlowers() {
        return flowers;
    }

    public int numFlowers() {
        return flowers.size();
    }

    public boolean hasFlowers() {
        return !flowers.isEmpty();
    }

    public boolean hasSeasonFlower() {
        return getFlowers().stream().anyMatch(a -> a.getTileType() == TileType.SEASON);
    }

    public boolean hasPlantFlower() {
        return getFlowers().stream().anyMatch(a -> a.getTileType() == TileType.PLANT);
    }

    /**
     * Returns the number of different flower tile types present in the hand, out of 2.
     * @return the number of tile types.
     */
    public int numFlowerTileTypes() {
        return (int) Stream.of(hasSeasonFlower(), hasPlantFlower())
                .filter(b -> b)
                .count();
    }

    // =============== PAIRS ===============

    public List<Meld> getPairs() {
        return meldsByMeldType.get(MeldType.PAIR);
    }

    public int numPairs() {
        return getPairs().size();
    }

    public List<Meld> getConcealedPairs() {
        return getConcealed(MeldType.PAIR);
    }

    public int numConcealedPairs() {
        return getConcealedPairs().size();
    }

    public List<Meld> getRevealedPairs() {
        return getRevealed(MeldType.PAIR);
    }

    public int numRevealedPairs() {
        return getRevealedPairs().size();
    }

    // =============== SHEUNGS ===============

    public List<Meld> getSheungs() {
        return meldsByMeldType.get(MeldType.SHEUNG);
    }

    public int numSheungs() {
        return getSheungs().size();
    }

    public List<Meld> getConcealedSheungs() {
        return getConcealed(MeldType.SHEUNG);
    }

    public int numConcealedSheungs() {
        return getConcealedSheungs().size();
    }

    public List<Meld> getRevealedSheungs() {
        return getRevealed(MeldType.SHEUNG);
    }

    public int numRevealedSheungs() {
        return getRevealedSheungs().size();
    }

    // =============== PONGS ===============

    public List<Meld> getPongs() {
        return meldsByMeldType.get(MeldType.PONG);
    }

    public int numPongs() {
        return getPongs().size();
    }

    public List<Meld> getConcealedPongs() {
        return getConcealed(MeldType.PONG);
    }

    public int numConcealedPongs() {
        return getConcealedPongs().size();
    }

    public List<Meld> getRevealedPongs() {
        return getRevealed(MeldType.PONG);
    }

    public int numRevealedPongs() {
        return getRevealedPongs().size();
    }

    // =============== KONGS ===============

    public List<Meld> getKongs() {
        return meldsByMeldType.get(MeldType.KONG);
    }

    public int numKongs() {
        return getKongs().size();
    }

    public List<Meld> getConcealedKongs() {
        return getConcealed(MeldType.KONG);
    }

    public int numConcealedKongs() {
        return getConcealedKongs().size();
    }

    public List<Meld> getRevealedKongs() {
        return getRevealed(MeldType.KONG);
    }

    public int numRevealedKongs() {
        return getRevealedKongs().size();
    }

    // =============== KONGS ===============

    public List<Meld> getPongsAndKongs() {
        return Stream.of(getPongs(), getKongs())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public int numPongsAndKongs() {
        return getPongsAndKongs().size();
    }

    public List<Meld> getConcealedPongsAndKongs() {
        return Stream.of(getConcealedPongs(), getRevealedKongs(), getConcealedKongs())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public int numConcealedPongsAndKongs() {
        return getConcealedPongsAndKongs().size();
    }

    public List<Meld> getTrulyConcealedPongsAndKongs() {
        return Stream.of(getConcealedPongs(), getConcealedKongs())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public int numTrulyConcealedPongsAndKongs() {
        return getTrulyConcealedPongsAndKongs().size();
    }

    // =============== OTHERS ===============

    public List<Meld> getThirteenOrphanGroups() {
        return meldsByMeldType.get(MeldType.THIRTEEN_ORPHANS);
    }

    public int numThirteenOrphanGroups() {
        return getThirteenOrphanGroups().size();
    }

    public List<Meld> getSixteenDisjointGroups() {
        return meldsByMeldType.get(MeldType.SIXTEEN_DISJOINT);
    }

    public int numSixteenDisjointGroups() {
        return getSixteenDisjointGroups().size();
    }

    public List<Meld> getWeirdMelds() {
        return meldsByMeldType.get(MeldType.OTHER);
    }

    // =============== BY MELD TYPE - NUMBER TILES ===============

    public List<Meld> getCircleMelds() {
        return meldsByTileType.get(TileType.CIRCLE);
    }

    public List<Meld> getBambooMelds() {
        return meldsByTileType.get(TileType.BAMBOO);
    }

    public List<Meld> getMillionMelds() {
        return meldsByTileType.get(TileType.MILLION);
    }

    /**
     * Returns all melds that use number tiles.
     * @return the list of melds.
     */
    public List<Meld> getNumberTileMelds() {
        return Stream.of(getCircleMelds(), getBambooMelds(), getMillionMelds())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of different number tile types present in the hand, out of 3.
     * @return the number of tile types.
     */
    public int numNumberTileTypes() {
        return (int) Stream.of(getCircleMelds(), getBambooMelds(), getMillionMelds())
                .filter(group -> !group.isEmpty())
                .count();
    }

    public boolean hasNumberTiles() {
        return !getNumberTileMelds().isEmpty();
    }

    // =============== BY MELD TYPE - WORD TILES ===============c

    public List<Meld> getWindMelds() {
        return meldsByTileType.get(TileType.WIND);
    }

    public List<Meld> getDragonMelds() {
        return meldsByTileType.get(TileType.DRAGON);
    }

    public List<Meld> getWordTileMelds() {
        return Stream.of(getWindMelds(), getDragonMelds())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns the number of different word tile types present in the hand, out of 2.
     * @return the number of tile types.
     */
    public int numWordTileTypes() {
        return (int) Stream.of(getWindMelds(), getDragonMelds())
                .filter(group -> !group.isEmpty())
                .count();
    }

    public boolean hasWordTiles() {
        return !getWordTileMelds().isEmpty();
    }

    // =============== MORE GETTERS ===============

    public List<Meld> getAllMelds() {
        return meldsByMeldType.values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    public List<Meld> getConcealedMelds() {
        return getAllMelds()
                .stream()
                .filter(m -> !m.isRevealed())
                .sorted(Comparator.comparing(m -> m.getMeldType() == MeldType.PAIR))
                .toList();
    }

    public int numConcealedMelds() {
        return getConcealedMelds().size();
    }

    public List<Meld> getRevealedMelds() {
        return getAllMelds()
                .stream()
                .filter(Meld::isRevealed)
                .sorted(Comparator.comparing(m -> m.getMeldType() == MeldType.PAIR))
                .toList();
    }

    public int numRevealedMelds() {
        return getRevealedMelds().size();
    }
}
