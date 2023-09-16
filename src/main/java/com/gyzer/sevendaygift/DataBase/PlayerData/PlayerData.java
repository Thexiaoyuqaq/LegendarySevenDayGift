package com.gyzer.sevendaygift.DataBase.PlayerData;

public class PlayerData {
    private String player;
    private int lastLogin;
    private int claimDay;
    private boolean claim;

    public PlayerData(String player, int lastLogin, int claimDay, boolean claim) {
        this.player = player;
        this.lastLogin = lastLogin;
        this.claimDay = claimDay;
        this.claim = claim;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setLastLogin(int lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setClaimDay(int claimDay) {
        this.claimDay = claimDay;
    }

    public void setClaim(boolean claim) {
        this.claim = claim;
    }

    public String getPlayer() {
        return player;
    }

    public int getLastLogin() {
        return lastLogin;
    }

    public int getClaimDay() {
        return claimDay;
    }

    public boolean isClaim() {
        return claim;
    }
}
