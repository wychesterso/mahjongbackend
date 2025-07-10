package com.mahjong.mahjongserver.auth;

public class UserAccount {
    private final String username;
    private String passwordHash; // ideally hashed

    public UserAccount(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        // Replace with hash check later
        return this.passwordHash.equals(password);
    }

    public void setPasswordHash(String newHash) {
        this.passwordHash = newHash;
    }
}