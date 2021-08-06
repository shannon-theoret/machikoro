package main;

public class Player {
    private int playerNumber;
    private boolean hasTrainStation = false;
    private boolean hasShoppingMall = false;
    private boolean hasAmusementPark = false;
    private boolean hasRadioTower = false;
    private PlayerStock stock = new PlayerStock();
    private boolean assigned = false;

    private int coins = 3;

    private Player playerToLeft;

    private boolean rolledDoubles = false;

    private boolean hasRolledOnce = false;

    public Player(int playerNumber) throws GameMechanicException {
        if (playerNumber > 3) {
            throw new GameMechanicException("Cannot have a player number greater than 3.");
        }
        this.playerNumber = playerNumber;
        stock.addCard(Card.WHEAT, 1);
        stock.addCard(Card.BAKERY, 1);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public boolean hasRolledOnce() {
        return hasRolledOnce;
    }

    public void setHasRolledOnce(boolean hasRolledOnce) {
        this.hasRolledOnce = hasRolledOnce;
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

    public void decreaseCoinCount(int amountToDecreaseBy) throws GameMechanicException {
        if (coins < amountToDecreaseBy) {
            throw new GameMechanicException("Cannot have a negative coing balance.");
        }
        coins -= amountToDecreaseBy;
    }

    public Player getPlayerToLeft() {
        return playerToLeft;
    }

    public void setPlayerToLeft(Player playerToLeft) {
        this.playerToLeft = playerToLeft;
    }

    public void purchaseLandmark(Landmark landmark) throws GameMechanicException {
        decreaseCoinCount(landmark.getCost());
        String duplicateLandmarkExceptionMessage = "This player already has this landmark.";
        switch (landmark) {
            case TRAIN_STATION:
                if (hasTrainStation) {
                    throw new GameMechanicException(duplicateLandmarkExceptionMessage);
                }
                hasTrainStation = true;
                break;
            case SHOPPING_MALL:
                if (hasShoppingMall) {
                    throw new GameMechanicException(duplicateLandmarkExceptionMessage);
                }
                hasShoppingMall = true;
                break;
            case AMUSEMENT_PARK:
                if (hasAmusementPark) {
                    throw new GameMechanicException(duplicateLandmarkExceptionMessage);
                }
                hasAmusementPark = true;
                break;
            case RADIO_TOWER:
                if (hasRadioTower) {
                    throw new GameMechanicException(duplicateLandmarkExceptionMessage);
                }
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
