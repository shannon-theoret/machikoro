package com.shannontheoret.machikoro.dto;

public class PlayerDTO {
    private Integer playerNumber;
    private String playerName;
    private Boolean isNPC;

    public Integer getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(Integer playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Boolean getIsNPC() {
        return isNPC;
    }

    public void setNPC(Boolean NPC) {
        isNPC = NPC;
    }
}
