package com.shannontheoret.machikoro;

public enum Landmark {
    TRAIN_STATION("train", 4),
    SHOPPING_MALL("shopping", 10),
    AMUSEMENT_PARK("amusement", 16),
    RADIO_TOWER("radio", 22);

    private String id;
    private int cost;

    Landmark(String id, int cost) {
        this.id = id;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public static Landmark getLandmarkFromId(String id) {
        for (Landmark landmark : Landmark.values()) {
            if (landmark.getId().equals(id)) {
                return landmark;
            }
        }
        return null;
    }
}
