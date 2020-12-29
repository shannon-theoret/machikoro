package main;

import java.util.HashMap;
import java.util.Map;

public class PlayerResponse {
    private boolean hasTrainStation;
    private boolean hasShoppingMall;
    private boolean hasAmusementPark;
    private boolean hasRadioTower;
    private Map<Integer, Integer> stock;
    private int coins;

    public PlayerResponse(Player player) {
        this.hasTrainStation = player.hasTrainStation();
        this.hasShoppingMall = player.hasShoppingMall();
        this.hasAmusementPark = player.hasAmusementPark();
        this.hasRadioTower = player.hasRadioTower();
        this.stock = player.getStock().getStockMap();
        this.coins = player.getCoins();
    }

    public boolean isHasTrainStation() {
        return hasTrainStation;
    }

    public void setHasTrainStation(boolean hasTrainStation) {
        this.hasTrainStation = hasTrainStation;
    }

    public boolean isHasShoppingMall() {
        return hasShoppingMall;
    }

    public void setHasShoppingMall(boolean hasShoppingMall) {
        this.hasShoppingMall = hasShoppingMall;
    }

    public boolean isHasAmusementPark() {
        return hasAmusementPark;
    }

    public void setHasAmusementPark(boolean hasAmusementPark) {
        this.hasAmusementPark = hasAmusementPark;
    }

    public boolean isHasRadioTower() {
        return hasRadioTower;
    }

    public void setHasRadioTower(boolean hasRadioTower) {
        this.hasRadioTower = hasRadioTower;
    }

    public Map<Integer, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Integer, Integer> stock) {
        this.stock = stock;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
