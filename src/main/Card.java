package main;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum Card {
    WHEAT(1, true, true, false, CardCategory.GRAIN, true, 1, 1),
    RANCH(2, true, true, false, CardCategory.COW, true, 1, 1),
    BAKERY(new HashSet<Integer>(Arrays.asList(2,3)), true, false, false, CardCategory.STORE, true, 1, 1),
    CAFE(3, false, false, true, CardCategory.CUP, true, 1, 2),
    CONVENIENCE_STORE(4, true, false, false, CardCategory.STORE, true, 3, 2),
    FOREST(5,true, true, false, CardCategory.GEAR, true, 1, 3),
    STADIUM(6, true, false, false, CardCategory.PURPLE, false, 2, 6),
    TV_STATION(6, true, false, false, CardCategory.PURPLE, false, 5, 7),
    CHEESE_FACTORY(7, true, false, false, CardCategory.OTHER, false, 3, 5),
    FURNITURE_FACTORY(8, true, false, false, CardCategory.OTHER, false, 3, 3),
    MINE(9, true, true, false, CardCategory.GEAR, true, 3, 6),
    FAMILY_RESTAURANT(new HashSet<Integer>(Arrays.asList(9, 10)), false, false, true, CardCategory.CUP, true, 2, 3),
    APPLE_ORCHARD(10, true, true, false, CardCategory.GRAIN, true, 3, 3),
    FRUIT_AND_VEGETABLE_GARDEN(new HashSet<Integer>(Arrays.asList(11, 12)), true, false, false, CardCategory.OTHER, false, 2, 2);

    private Set<Integer> rolls;
    private boolean onPlayersTurn;
    private boolean onAnyonesTurn;
    private boolean steals;
    private CardCategory category;
    private boolean isBasic;
    private int amountGained;
    private int cost;

    Card(int roll, boolean onPlayersTurn, boolean onAnyonesTurn, boolean steals, CardCategory category, boolean isBasic, int amountGained, int cost) {
        this.rolls = new HashSet<Integer>();
        this.rolls.add(roll);
        this.onPlayersTurn = onPlayersTurn;
        this.onAnyonesTurn = onAnyonesTurn;
        this.steals = steals;
        this.category = category;
        this.isBasic = isBasic;
        this.amountGained = amountGained;
        this.cost = cost;
    }

    Card(Set<Integer> rolls, boolean onPlayersTurn, boolean onAnyonesTurn, boolean steals, CardCategory category, boolean isBasic, int amountGained, int cost) {
        this.rolls = rolls;
        this.onPlayersTurn = onPlayersTurn;
        this.onAnyonesTurn = onAnyonesTurn;
        this.steals = steals;
        this.category = category;
        this.isBasic = isBasic;
        this.amountGained = amountGained;
    }

    public boolean rollApplies(Integer roll) {
        return rolls.contains(roll);
    }

    public boolean isOnPlayersTurn() {
        return onPlayersTurn;
    }

    public boolean isOnAnyonesTurn() {
        return onAnyonesTurn;
    }

    public boolean isSteals() {
        return steals;
    }

    public CardCategory getCategory() {
        return category;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public int getAmountGained() {
        return amountGained;
    }

    public int getCost() {
        return cost;
    }

    public Set<Integer> getRolls() { return rolls; }

    public static Set<Card> firstHalfOfGameCards() {
        return EnumSet.range(WHEAT, TV_STATION);
    }

}

