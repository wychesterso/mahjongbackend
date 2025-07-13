package com.mahjong.mahjongserver.domain.player;

public class Player {
    private int id;
    private String username;
    private int score;

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
        this.score = 1000;
    }

//============================== GETTERS AND SETTERS ==============================//

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//============================== UPDATE SCORE ==============================//

    public void addScore(int addedScore) {
        score += addedScore;
    }

    public void deductScore(int deductedScore) {
        score = Math.max(score - deductedScore, 0);
    }
}
