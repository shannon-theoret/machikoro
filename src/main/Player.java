package main;

public class Player {
    private boolean hasTrainStation = false;
    private boolean hasShoppingMall = false;
    private boolean hasAmusementPark = false;
    private boolean hasRadioTower = false;
    private PlayerStock stock = new PlayerStock();

    private int coins = 3;

    private Player playerToLeft;

    private boolean rolledDoubles = false;

    private boolean hasRolledOnce = false;

    public boolean hasRolledOnce() {
        return hasRolledOnce;
    }

    public void setHasRolledOnce(boolean hasRolledOnce) {
        this.hasRolledOnce = hasRolledOnce;
    }

    Player() {
        stock.addCard(Card.WHEAT, 1);
        stock.addCard(Card.BAKERY, 1);
    }

    public boolean hasTrainStation() {
        return hasTrainStation;
    }

    public void setHasTrainStation(boolean hasTrainStation) {
        this.hasTrainStation = hasTrainStation;
    }

    public boolean hasShoppingMall() {
        return hasShoppingMall;
    }

    public void setHasShoppingMall(boolean hasShoppingMall) {
        this.hasShoppingMall = hasShoppingMall;
    }

    public boolean hasAmusementPark() {
        return hasAmusementPark;
    }

    public void setHasAmusementPark(boolean hasAmusementPark) {
        this.hasAmusementPark = hasAmusementPark;
    }

    public boolean hasRadioTower() {
        return hasRadioTower;
    }

    public void setHasRadioTower(boolean hasRadioTower) {
        this.hasRadioTower = hasRadioTower;
    }

    public PlayerStock getStock() {
        return stock;
    }

    public void setStock(PlayerStock stock) {
        this.stock = stock;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean hasWon() {
        return (hasTrainStation && hasShoppingMall && hasAmusementPark && hasRadioTower);
    }

    public void increaseCoinCount(int amountToIncreaseBy) {
        coins += amountToIncreaseBy;
    }

    public void decreaseCoinCount(int amountToDecreaseBy) {
        coins -= amountToDecreaseBy;
    }

    public Player getPlayerToLeft() {
        return playerToLeft;
    }

    public void setPlayerToLeft(Player playerToLeft) {
        this.playerToLeft = playerToLeft;
    }

    public void purchaseLandmark(Landmark landmark) {
        decreaseCoinCount(landmark.getCost());
        switch (landmark) {
            case TRAIN_STATION:
                hasTrainStation = true;
                break;
            case SHOPPING_MALL:
                hasShoppingMall = true;
                break;
            case AMUSEMENT_PARK:
                hasAmusementPark = true;
                break;
            case RADIO_TOWER:
                hasRadioTower = true;
                break;
        }
    }

    public boolean isRolledDoubles() {
        return rolledDoubles;
    }

    public void setRolledDoubles(boolean rolledDoubles) {
        this.rolledDoubles = rolledDoubles;
    }
}
