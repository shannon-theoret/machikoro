package com.shannontheoret.machikoro;

public enum Landmark {
    TRAIN_STATION(4),
    SHOPPING_MALL(10),
    AMUSEMENT_PARK(16),
    RADIO_TOWER(22);

    private int cost;
    public final static int TOTAL_COST = 52;

    Landmark(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public double getProgress() {
        return (double) cost/(double) TOTAL_COST;
    }

}
