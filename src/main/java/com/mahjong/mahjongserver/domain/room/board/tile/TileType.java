package com.mahjong.mahjongserver.domain.room.board.tile;

/**
 * A collection of the different constant tile types.
 */
public enum TileType {
    /** 🀙🀚🀛🀜🀝🀞🀟🀠🀡 */
    CIRCLE,

    /** 🀐🀑🀒🀓🀔🀕🀖🀗🀘 */
    BAMBOO,

    /** 🀇🀈🀉🀊🀋🀌🀍🀎🀏 */
    MILLION,

    /** 🀀🀁🀂🀃 */
    WIND,

    /** 🀄🀅🀆 */
    DRAGON,

    /** 🀦🀧🀨🀩 */
    SEASON,

    /** 🀢🀣🀤🀥 */
    PLANT,
    ;

    /**
     * Retrieves the classification of this tile type.
     * @return the tile classification.
     */
    public TileClassification getClassification() {
        switch (this) {
            case WIND, DRAGON -> {
                return TileClassification.WORD;
            }
            case SEASON, PLANT -> {
                return TileClassification.FLOWER;
            }
            default -> {
                return TileClassification.REGULAR;
            }
        }
    }
}
