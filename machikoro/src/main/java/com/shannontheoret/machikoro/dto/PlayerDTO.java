package com.shannontheoret.machikoro.dto;

import com.shannontheoret.machikoro.StrategyName;

import java.util.Map;

public class PlayerDTO {
    private Integer playerNumber;
    private String playerName;
    private Boolean isNPC;
    private Boolean chooseStrategy;
    private Map<StrategyName, Integer> strategy;

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

    public Boolean getChooseStrategy() {
        return chooseStrategy;
    }

    public void setChooseStrategy(Boolean chooseStrategy) {
        this.chooseStrategy = chooseStrategy;
    }

    public Map<StrategyName, Integer> getStrategy() {
        return strategy;
    }

    public void setStrategy(Map<StrategyName, Integer> strategy) {
        this.strategy = strategy;
    }
}
