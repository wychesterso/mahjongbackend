package com.mahjong.mahjongserver.dto;

public class PlayerEarningsDTO {
    private final String playerName;
    private final String seat;
    private final int originalScore;
    private final int finalScore;
    private final int netEarnings;

    public PlayerEarningsDTO(String playerName, String seat, int originalScore, int finalScore) {
        this.playerName = playerName;
        this.seat = seat;
        this.originalScore = originalScore;
        this.finalScore = finalScore;
        this.netEarnings = finalScore - originalScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSeat() {
        return seat;
    }

    public int getOriginalScore() {
        return originalScore;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getNetEarnings() {
        return netEarnings;
    }
}