package com.mahjong.mahjongserver.domain.room.board.tile;

/**
 * A collection of constant Mahjong tiles used in the game.
 */
public enum Tile {
    CIRCLE_1(TileType.CIRCLE, 1, "🀙"),
    CIRCLE_2(TileType.CIRCLE, 2, "🀚"),
    CIRCLE_3(TileType.CIRCLE, 3, "🀛"),
    CIRCLE_4(TileType.CIRCLE, 4, "🀜"),
    CIRCLE_5(TileType.CIRCLE, 5, "🀝"),
    CIRCLE_6(TileType.CIRCLE, 6, "🀞"),
    CIRCLE_7(TileType.CIRCLE, 7, "🀟"),
    CIRCLE_8(TileType.CIRCLE, 8, "🀠"),
    CIRCLE_9(TileType.CIRCLE, 9, "🀡"),

    BAMBOO_1(TileType.BAMBOO, 1, "🀐"),
    BAMBOO_2(TileType.BAMBOO, 2, "🀑"),
    BAMBOO_3(TileType.BAMBOO, 3, "🀒"),
    BAMBOO_4(TileType.BAMBOO, 4, "🀓"),
    BAMBOO_5(TileType.BAMBOO, 5, "🀔"),
    BAMBOO_6(TileType.BAMBOO, 6, "🀕"),
    BAMBOO_7(TileType.BAMBOO, 7, "🀖"),
    BAMBOO_8(TileType.BAMBOO, 8, "🀗"),
    BAMBOO_9(TileType.BAMBOO, 9, "🀘"),

    MILLION_1(TileType.MILLION, 1, "🀇"),
    MILLION_2(TileType.MILLION, 2, "🀈"),
    MILLION_3(TileType.MILLION, 3, "🀉"),
    MILLION_4(TileType.MILLION, 4, "🀊"),
    MILLION_5(TileType.MILLION, 5, "🀋"),
    MILLION_6(TileType.MILLION, 6, "🀌"),
    MILLION_7(TileType.MILLION, 7, "🀍"),
    MILLION_8(TileType.MILLION, 8, "🀎"),
    MILLION_9(TileType.MILLION, 9, "🀏"),

    EAST(TileType.WIND, 1, "🀀"),
    SOUTH(TileType.WIND, 2, "🀁"),
    WEST(TileType.WIND, 3, "🀂"),
    NORTH(TileType.WIND, 4, "🀃"),
    RED_DRAGON(TileType.DRAGON, 5, "🀄"),
    GREEN_DRAGON(TileType.DRAGON, 6, "🀅"),
    WHITE_DRAGON(TileType.DRAGON, 7, "🀆"),

    FLOWER_SPRING(TileType.SEASON, 1, "🀦"),
    FLOWER_SUMMER(TileType.SEASON, 2, "🀧"),
    FLOWER_AUTUMN(TileType.SEASON, 3, "🀨"),
    FLOWER_WINTER(TileType.SEASON, 4, "🀩"),
    FLOWER_MUI(TileType.PLANT, 5, "🀢"),
    FLOWER_LAN(TileType.PLANT, 6, "🀣"),
    FLOWER_KUK(TileType.PLANT, 7, "🀥"),
    FLOWER_CHUK(TileType.PLANT, 8, "🀤");

    private final TileType tileType;
    private final int tileNum;
    private final String tileString;

    /**
     * Creates a new tile.
     * @param tileType the type of the tile.
     * @param tileNum the number of the tile within its own type.
     * @param tileString the tile's unicode character.
     */
    Tile(TileType tileType, int tileNum, String tileString) {
        this.tileType = tileType;
        this.tileNum = tileNum;
        this.tileString = tileString;
    }
    
    /**
     * Retrieves the tile's type.
     * @return the type of the tile.
     */
    public TileType getTileType() {
        return tileType;
    }

    /**
     * Retrieves the tile's number within its own type.
     * @return the number of the tile.
     */
    public int getTileNum() {
        return tileNum;
    }

    /**
     * Retrieves the unicode character of the tile.
     * @return the unicode character of the tile.
     */
    public String getTileString() {
        return tileString;
    }
}