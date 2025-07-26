package com.mahjong.mahjongserver.domain.player;

public class PlayerProfile {
    private final String playerId;
    private int totalScore;

    public PlayerProfile(String playerId, int totalScore) {
        this.playerId = playerId;
        this.totalScore = totalScore;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}