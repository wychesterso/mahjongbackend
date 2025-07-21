package com.mahjong.mahjongserver.domain.player;

public abstract class Player {
    private String id;
    private int score;

    public Player(String id) {
        this.id = id;
        this.score = 1000;
    }

//============================== GETTERS AND SETTERS ==============================//

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract boolean isBot();

//============================== UPDATE SCORE ==============================//

    public void addScore(int addedScore) {
        score += addedScore;
    }

    public void deductScore(int deductedScore) {
        score = Math.max(score - deductedScore, 0);
    }
}
