package com.mahjong.mahjongserver.domain.game.score.data;

public enum ScoringPattern {
    // ==================== ONE POINT PATTERNS ====================
    DEALER("莊", "Dealer", 1),
    SELF_DRAW("自摸", "Self-Draw", 1),
    SELF_DRAW_AFTER_DRAWING_FLOWER("花上自摸", "Self-Draw after drawing flower tile", 1),
    SELF_DRAW_AFTER_KONG("摃上自摸", "Self-Draw after performing Kong", 1),
    ROBBING_KONG("搶摃吃糊", "Win by robbing Kong", 1),

    NO_FLOWERS("無花", "No flowers", 1),
    NO_WORDS("無字", "No word tiles in hand", 1),
    FLOWER("爛花", "Regular flower tile", 1),
    MELDED_KONG("明摃", "Melded Kong", 1),

    EAST_WIND("東風", "East Wind", 1),
    SOUTH_WIND("南風", "South Wind", 1),
    WEST_WIND("西風", "West Wind", 1),
    NORTH_WIND("北風", "North Wind", 1),

    WAIT_ON_TWO_PAIRS("對碰", "Waiting on two different tiles to match two different pairs", 1),
    SINGLE_WAIT_ON_TWO_MELDS("假獨", "Waiting on a single tile fitting multiple pairs", 1),
    GOOD_PAIR("將眼", "Pair is 2, 5 or 8", 1),

    // ==================== TWO POINT PATTERNS ====================
    MATCHING_FLOWER("正花", "Flower matching seat ordinal", 2),
    CONCEALED_KONG("暗摃", "Concealed Kong", 2),

    RED_DRAGON("紅中", "Red Dragon", 2),
    GREEN_DRAGON("發財", "Green Dragon", 2),
    WHITE_DRAGON("白板", "White Dragon", 2),

    SINGLE_WAIT("獨獨", "Waiting on a single tile", 2),
    LO_SIU("老少", "123 & 789 or 111 & 999 in same suit", 2),
    TWO_ENCOUNTERS("二相逢", "Two Sheungs of same sequence but different suits", 2),

    // ==================== THREE POINT PATTERNS ====================
    ALL_SHEUNGS("平糊", "All groups are Sheungs", 3),
    ALL_CONCEALED("門前清", "Entire hand concealed", 3),

    TWO_CONCEALED_PONGS("二暗刻", "Exactly two of concealed Pongs + Kongs", 3),
    TWO_IDENTICAL_SHEUNGS("一般高", "Two identical Sheungs of same sequence and suit", 3),
    TWO_BROTHERS("二兄弟", "Two Pongs/Kongs of same number", 3),

    // ==================== FIVE POINT PATTERNS ====================
    NO_WORDS_OR_FLOWERS("無字花", "No word or flower tiles", 5),
    ALL_CONCEALED_SELF_DRAW("門清自摸", "Self-draw and no revealed groups", 5),

    TWO_SUITS("缺一門", "Only contains two of the following: Tung, Sok, Maan",5),
    FIVE_SUITS("五門齊", "Contains all of Tung, Sok, Maan, Wind, and Dragon", 5),
    NO_WONDERS("斷么", "No 1, 9, or word tiles", 5),
    FOUR_TILES_TWO_GROUPS("四歸一", "Four identical tiles used in two different groups", 5),

    // ==================== EIGHT POINT PATTERNS ====================
    ALL_MELDED_SELF_DRAW("半求人", "All groups are revealed, and won off self-draw", 8),
    LITTLE_THREE_SISTERS("小三姊妹", "Two Pongs/Kongs of same type and adjacent numbers, plus a pair adjacent to it", 8),
    MELDED_MIXED_DRAGON("明雜龍", "123, 456, 789 groups of three different types", 8),

    // ==================== TEN POINT PATTERNS ====================
    MULTIPLE_WINNERS("雙響", "Multiple winners", 10),
    FLOWER_SET("一台花", "Four flower tiles of the same type", 10),
    TEN_REMAINING_TILES("十只內", "Win with ten or less tiles in the wall", 10),
    CHICKEN_HAND("雞糊", "Chicken Hand", 10),
    ALL_SHEUNGS_NO_WORDS_OR_FLOWERS("大平糊", "No word or flower tiles, all groups are Sheungs", 10),
    SEVEN_SUITS("七門齊", "Contains all of Tung, Sok, Maan, Wind, Dragon, and both flower types", 10),

    THREE_CONCEALED_PONGS("三暗刻", "Three groups of 3+ identical tiles, unrevealed", 10),
    THREE_ENCOUNTERS("三相逢", "Three Sheungs of same numbers but different types", 10),
    LITTLE_THREE_BROTHERS("小三兄弟", "Two Pongs/Kongs plus a pair of same number", 10),

    FOUR_TILES_THREE_GROUPS("四歸二", "Four identical tiles used in three different groups", 10),
    MELDED_DRAGON("明龍", "123, 456, 789 groups of the same type", 10),
    ALL_USING_WONDERS("全帶混么", "Every group/pair has 1, 9, or word tiles", 10),

    // ==================== FIFTEEN POINT PATTERNS ====================
    THREE_IDENTICAL_SHEUNGS("三般高", "Three identical Sheungs of same sequence and suit", 15),
    BIG_THREE_BROTHERS("大三兄弟", "Three Pongs/Kongs of same number", 15),
    BIG_THREE_SISTERS("大三姊妹", "Three Pongs/Kongs of same type and adjacent numbers", 15),

    CONCEALED_MIXED_DRAGON("暗雜龍", "123, 456, 789 groups of three different types, none revealed", 15),
    ALL_USING_WONDERS_NO_WORDS("全帶么", "Every group/pair has 1 or 9, no word tiles", 15),
    ALL_MELDED("全求人", "All groups are revealed, and won off another player", 15),

    LITTLE_THREE_WINDS("小三風", "Contains two Pongs/Kongs and one pair of the four Wind tiles", 15),

    // ==================== TWENTY POINT PATTERNS ====================
    FIVE_REMAINING_TILES("五只內", "Win with five or less tiles in the wall", 20),
    WIN_ON_LAST_DRAW("海底撈月", "Win on last tile draw", 20),

    FOUR_TILES_FOUR_GROUPS("四歸四", "Four identical tiles used in four different groups", 20),
    FOUR_ENCOUNTERS("四同順", "Four Sheungs of same numbers", 20),
    CONCEALED_DRAGON("暗龍", "123, 456, 789 groups of the same type, none revealed", 20),
    LITTLE_THREE_DRAGONS("小三元", "Contains two Pongs/Kongs and one pair of the three Dragon tiles", 20),

    // ==================== THIRTY POINT PATTERNS ====================
    SELF_DRAW_AFTER_TWO_KONGS("摃上摃自摸", "Self-Draw after two consecutive Kongs", 30),
    SELF_DRAW_AFTER_ROBBING_KONG_AND_KONG("搶摃上摃自摸", "Self-Draw after Kong after stealing from Kong", 30),
    FLOWER_WIN("兩台花", "All flower tiles", 30),

    FOUR_CONCEALED_PONGS("四暗刻", "Four groups of 3+ identical tiles, unrevealed", 30),
    FOUR_IDENTICAL_SHEUNGS("四般高", "Four identical Sheungs", 30),

    ALL_PONGS("對對糊", "All groups are Pongs/Kongs", 30),
    HALF_FLUSH("混一色", "Only contains word tiles and one of the following: Tung, Sok, Maan", 30),
    ALL_WONDERS("混么", "All tiles are 1, 9, or word tiles", 30),
    BIG_THREE_WINDS("大三風", "Contains three Pongs/Kongs of the four Wind tiles", 30),

    // ==================== FORTY POINT PATTERNS ====================
    FIVE_ENCOUNTERS("五同順", "Five Sheungs of same numbers", 40),
    BIG_THREE_DRAGONS("大三元", "Contains three Pongs/Kongs of the three Dragon tiles", 40),
    SIXTEEN_ORPHANS("十六不搭", "Sixteen tiles that cannot form any group with one another, with one duplicate tile", 40),
    LIK_KU_LIK_KU("嚦咕嚦咕", "Seven pairs and one Pong, all concealed", 40),

    // ==================== SIXTY POINT PATTERNS ====================
    LITTLE_FOUR_WINDS("小四喜", "Contains three Pongs/Kongs and one pair of the four Wind tiles", 60),

    // ==================== EIGHTY POINT PATTERNS ====================
    DEI_WU("地糊", "Win on first discard", 80),
    YAN_WU("人糊", "Win within four discards", 80),

    FIVE_CONCEALED_PONGS("五暗刻", "Five groups of 3+ identical tiles, unrevealed", 80),
    FLUSH("清一色", "Only contains one of the following: Tung, Sok, Maan", 80),
    ALL_WONDERS_NO_WORDS("清么", "All tiles are 1 or 9", 80),
    BIG_FOUR_WINDS("大四喜", "Contains four Pongs/Kongs of the four Wind tiles", 80),
    THIRTEEN_WONDERS("十三么", "The thirteen orphan tiles with one duplicate tile, plus any unrevealed group", 80),

    // ==================== HUNDRED POINT PATTERNS ====================
    ALL_CONCEALED_PONGS_SELF_DRAW("坎坎糊", "Self-draw, no revealed groups, and all Pongs/Kongs", 100),
    TIN_WU("天糊", "Zhong win on first draw", 100),
    ;

    private final String name;
    private final String description;
    private int value;

    ScoringPattern(String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int score) {
        this.value = score;
    }

    public void addValue(int amount) {
        this.value += amount;
    }

    public String getDescription() {
        return description;
    }
}