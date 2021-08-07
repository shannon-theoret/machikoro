package main;

import java.util.*;

public class Game {
    private Player player1;
    private Player player2;
    private Player player3;
    private Player currentPlayer;
    private Map<String, Player> namedPlayers = new HashMap<>();
    private Step step;
    private List<Integer> recentRoll;
    private Map<Player, NPC> npcs;

    private Map<Card, Integer> gameStock = new HashMap<>();


    public Game() throws GameMechanicException {
        npcs = new HashMap<>();
        player1 = new Player(1, false);
        player2 = new Player(2, true);
        npcs.put(player2, new SimpleNPC());
        player3 = new Player(3, true);
        npcs.put(player3, new SimpleNPC());
        currentPlayer = player1;
        step = Step.ROLL;
        player1.setPlayerToLeft(player2);
        player2.setPlayerToLeft(player3);
        player3.setPlayerToLeft(player1);
        recentRoll = new ArrayList<>();

        for (Card card : Card.values()) {
            if (card.getCategory() == CardCategory.PURPLE) {
                gameStock.put(card, 3);
            } else {
                gameStock.put(card, 8);
            }
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getPlayer3() {
        return player3;
    }

    public void setPlayer3(Player player3) {
        this.player3 = player3;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Integer getCurrentPlayerNumeric() {
        return currentPlayer.getPlayerNumber();
    }

    public Player getPlayerFromNumber(int number) throws GameMechanicException {
        switch (number) {
            case 1:
                return player1;
            case 2:
                return player2;
            case 3:
                return player3;
        }
        throw new GameMechanicException("Player number must be 1, 2 or 3.");
    }

    public Map<Card, Integer> getGameStock() {
        return gameStock;
    }

    public void setGameStock(Map<Card, Integer> gameStock) {
        this.gameStock = gameStock;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public List<Integer> getRecentRoll() {
        return recentRoll;
    }

    public void setRecentRoll(List<Integer> recentRoll) throws GameMechanicException {
        if (recentRoll.size() > 2 ) {
            throw new GameMechanicException("Only two dice may be thrown.");
        }
        for (Integer roll : recentRoll) {
            if (roll < 1 || roll > 6) {
                throw new GameMechanicException("Die must be between 1 and 6.");
            }
        }
        this.recentRoll = recentRoll;
    }

    public void rollSingle() throws GameMechanicException {
        Integer dieRoll = generateRandomDieRoll();
        List<Integer> recentSingleRoll = new ArrayList<>();
        recentSingleRoll.add(dieRoll);
        setRecentRoll(recentSingleRoll);
        currentPlayer.setRolledDoubles(false);
        if (currentPlayer.hasRadioTower() && !currentPlayer.hasRolledOnce()) {
            currentPlayer.setHasRolledOnce(true);
            this.step = Step.CONFIRM_ROLL;
            return;
        }
        handleRoll(dieRoll);
        if (this.step != Step.STEAL) {
            this.step = Step.BUY;
        }
    }

    public void rollDouble() throws GameMechanicException {
        Integer dieOne = generateRandomDieRoll();
        Integer dieTwo = generateRandomDieRoll();
        if (dieOne == dieTwo) {
            currentPlayer.setRolledDoubles(true);
        } else {
            currentPlayer.setRolledDoubles(false);
        }
        List<Integer> recentDoubleRoll = new ArrayList<>();
        recentDoubleRoll.add(dieOne);
        recentDoubleRoll.add(dieTwo);
        setRecentRoll(recentDoubleRoll);
        if (currentPlayer.hasRadioTower() && !currentPlayer.hasRolledOnce()) {
            currentPlayer.setHasRolledOnce(true);
            this.step = Step.CONFIRM_ROLL;
            return;
        }
        handleRoll(dieOne + dieTwo);
        if (this.step != Step.STEAL) {
            this.step = Step.BUY;
        }
    }

    public void confirmRoll() throws GameMechanicException {
        Integer dieOne = 0;
        Integer dieTwo = 0;
        if (getRecentRoll().size() == 1) {
            dieOne = getRecentRoll().get(0);
        } else if (getRecentRoll().size() == 2) {
            dieOne = getRecentRoll().get(0);
            dieTwo = getRecentRoll().get(1);
        }
        if (dieOne == dieTwo) {
            currentPlayer.setRolledDoubles(true);
        } else {
            currentPlayer.setRolledDoubles(false);
        }
        handleRoll(dieOne + dieTwo);
        if (this.step != Step.STEAL) {
            this.step = Step.BUY;
        }
    }

    public void handleRoll(int roll) throws GameMechanicException {
            // handle red cards first
            Player playerToSteal = currentPlayer.getPlayerToLeft();
            while (!playerToSteal.equals(currentPlayer)) {
                Set<Card> releventCards = playerToSteal.getStock().getRedCardsForRoll(roll);
                for (Card card: releventCards) {
                    if (card.isBasic()) {
                        int amountGainedPerCard = card.getAmountGained();
                        if (playerToSteal.hasShoppingMall() && card.getCategory() == CardCategory.CUP) {
                            amountGainedPerCard++;
                        }
                        int amountToSteal = amountGainedPerCard * playerToSteal.getStock().getCardCount(card);
                        if (currentPlayer.getCoins() < amountToSteal) {
                            amountToSteal = currentPlayer.getCoins();
                        }
                        currentPlayer.decreaseCoinCount(amountToSteal);
                        playerToSteal.increaseCoinCount(amountToSteal);
                    }
                }
                playerToSteal = playerToSteal.getPlayerToLeft();
            }
            // handle green and blue cards for current character
            Set<Card> releventCards = currentPlayer.getStock().getCardsForPlayerRoll(roll);
            for (Card card : releventCards) {
                int amountToAdd = 0;
                if (card.isBasic()) {
                    int amountGainedPerCard = card.getAmountGained();
                    if(currentPlayer.hasShoppingMall() && card.getCategory() == CardCategory.STORE) {
                        amountGainedPerCard++;
                    }
                    amountToAdd = amountGainedPerCard * currentPlayer.getStock().getCardCount(card);
                } else if (card == Card.CHEESE_FACTORY) {
                    int numberOfCows = currentPlayer.getStock().getCountOfCardsInCategory(CardCategory.COW);
                    amountToAdd = numberOfCows * card.getAmountGained() * currentPlayer.getStock().getCardCount(card);
                } else if (card == Card.FURNITURE_FACTORY) {
                    int countOfGearCards = currentPlayer.getStock().getCountOfCardsInCategory(CardCategory.GEAR);
                    amountToAdd = countOfGearCards * card.getAmountGained() * currentPlayer.getStock().getCardCount(card);
                } else if (card == Card.FRUIT_AND_VEGETABLE_GARDEN) {
                    int countOfGrainCards = currentPlayer.getStock().getCountOfCardsInCategory(CardCategory.GRAIN);
                    amountToAdd = countOfGrainCards * card.getAmountGained() * currentPlayer.getStock().getCardCount(card);
                }
                currentPlayer.increaseCoinCount(amountToAdd);
            }

            //handle blue cards for other players
            Player otherPlayer = currentPlayer.getPlayerToLeft();
            while(!otherPlayer.equals(currentPlayer)) {
                releventCards = otherPlayer.getStock().getBlueCardsForRoll(roll);
                for (Card card : releventCards) {
                    if (card.isBasic()) {
                        int amountToAdd = card.getAmountGained() * otherPlayer.getStock().getCardCount(card);
                        otherPlayer.increaseCoinCount(amountToAdd);
                    }
                }
                otherPlayer = otherPlayer.getPlayerToLeft();
            }

            //handle purple cards for current player
            if (currentPlayer.getStock().getCardCount(Card.STADIUM) != 0 && Card.STADIUM.rollApplies(roll)) {
                Player playerToStealFrom = currentPlayer.getPlayerToLeft();
                while (!playerToStealFrom.equals(currentPlayer)) {
                    int amountToSteal = 0;
                    if (playerToStealFrom.getCoins() < Card.STADIUM.getAmountGained()) {
                        amountToSteal = playerToStealFrom.getCoins();
                    } else {
                        amountToSteal = Card.STADIUM.getAmountGained();
                    }
                    playerToStealFrom.decreaseCoinCount(amountToSteal);
                    currentPlayer.increaseCoinCount(amountToSteal);
                    playerToStealFrom = playerToStealFrom.getPlayerToLeft();
                }
            }

            if (currentPlayer.getStock().getCardCount(Card.TV_STATION) != 0 && Card.TV_STATION.rollApplies(roll)) {
                step = Step.STEAL;
            }

        }

        public void steal(int playerNumberToStealFrom) throws GameMechanicException {
            Player playerToStealFrom = getPlayerFromNumber(playerNumberToStealFrom);
            int amountToSteal = 0;
            if (playerToStealFrom.getCoins() < Card.TV_STATION.getAmountGained()) {
                amountToSteal = playerToStealFrom.getCoins();
            } else {
                amountToSteal = Card.TV_STATION.getAmountGained();
            }
            playerToStealFrom.decreaseCoinCount(amountToSteal);
            currentPlayer.increaseCoinCount(amountToSteal);
            step = Step.BUY;
        }

        public void purchaseCard(Integer index) throws GameMechanicException {
            Card cardToPurchase = Card.values()[index];
            purchaseCard(cardToPurchase);
        }

        public void purchaseCard(Card card) throws GameMechanicException {
            currentPlayer.getStock().addCard(card);
            currentPlayer.decreaseCoinCount(card.getCost());
            Integer stockQuantity = gameStock.get(card);
            stockQuantity--;
            if (stockQuantity < 0) {
                throw new GameMechanicException("Card not in stock.");
            }
            gameStock.put(card, stockQuantity);
            endTurn();
        }

        public void purchaseLandmark(String id) throws GameMechanicException {
            Landmark landmark = Landmark.getLandmarkFromId(id);
            purchaseLandmark(landmark);
        }

        public void purchaseLandmark(Landmark landmark) throws GameMechanicException {
            currentPlayer.purchaseLandmark(landmark);
            endTurn();
        }

        public void endTurn() {
            if (currentPlayer.hasWon()) {
                step = Step.WON;
                return;
            }
            if (!(currentPlayer.hasAmusementPark() && currentPlayer.isRolledDoubles())) {
                currentPlayer.setHasRolledOnce(false);
                currentPlayer = currentPlayer.getPlayerToLeft();
            }
            step = Step.ROLL;
        }

        public void npcMove() throws GameMechanicException, InvalidMoveException {
            if (!currentPlayer.isNPC()) {
                throw new InvalidMoveException("Player " + currentPlayer.getPlayerNumber() + " is not an NPC.");
            }
            if (!npcs.containsKey(currentPlayer)) {
                throw new GameMechanicException("No strategy assigned to Player " + currentPlayer.getPlayerNumber());
            }
            NPC strategy = npcs.get(currentPlayer);

            switch (step) {
                case ROLL:
                    if (strategy.rollSingleDice()) {
                        rollSingle();
                    } else {
                        rollDouble();
                    }
                    return;
                case BUY:
                    Decision decision = strategy.makeBuyingDecision();
                    switch (decision) {
                        case CARD:
                            purchaseCard(strategy.chooseCard());
                            return;
                        case LANDMARK:
                            purchaseLandmark(strategy.chooseLandmark());
                            return;
                        case END_TURN:
                            endTurn();
                            return;
                    }
                case STEAL:
                    steal(strategy.choosePlayerToStealFrom());
                    return;
                case CONFIRM_ROLL:
                    if (strategy.reroll()) {
                        if (strategy.rollSingleDice()) {
                            rollSingle();
                        } else {
                            rollDouble();
                        }
                    } else {
                        confirmRoll();
                    }
                    return;
                case WON:
                    throw new InvalidMoveException("The game is over.");
            }


        }

        private static Integer generateRandomDieRoll() {
            Random r = new Random();
            return r.nextInt(6) + 1;
        }

        public enum Decision {
            LANDMARK,
            CARD,
            END_TURN;
        }

        public interface NPC {

            boolean rollSingleDice();

            boolean reroll();

            int choosePlayerToStealFrom();

            Decision makeBuyingDecision();

            Landmark chooseLandmark() throws InvalidMoveException;

            Card chooseCard() throws InvalidMoveException;

        }

        public class SimpleNPC implements NPC {

            @Override
            public boolean rollSingleDice() {
                if (currentPlayer.hasTrainStation()) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean reroll() {
                Integer recentRollValue = 0;
                for (Integer roll: recentRoll) {
                    recentRollValue += roll;
                }
                if (currentPlayer.getStock().getCardsForPlayerRoll(recentRollValue).isEmpty()) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public int choosePlayerToStealFrom() {
                Player nonCurrentPlayerWithMostCoins =
                        (currentPlayer.getPlayerToLeft().getCoins() > currentPlayer.getPlayerToLeft().getPlayerToLeft().getCoins()?
                                currentPlayer.getPlayerToLeft() : currentPlayer.getPlayerToLeft().getPlayerToLeft());
                return nonCurrentPlayerWithMostCoins.getPlayerNumber();

            }

            @Override
            public Decision makeBuyingDecision() {
                if ((currentPlayer.getCoins() > Landmark.RADIO_TOWER.getCost() && !currentPlayer.hasRadioTower()) ||
                        (currentPlayer.getCoins() > Landmark.AMUSEMENT_PARK.getCost() && !currentPlayer.hasAmusementPark()) ||
                        (currentPlayer.getCoins() > Landmark.SHOPPING_MALL.getCost() && !currentPlayer.hasShoppingMall()) ||
                        (currentPlayer.getCoins() > Landmark.TRAIN_STATION.getCost() && !currentPlayer.hasTrainStation()
                        && currentPlayer.getStock().getNumberOfCards() > 8)) {
                    return Decision.LANDMARK;
                } else {
                    return Decision.CARD;
                }
            }

            @Override
            public Landmark chooseLandmark() throws InvalidMoveException {
                if (currentPlayer.getCoins() >= Landmark.RADIO_TOWER.getCost() && !currentPlayer.hasRadioTower()) {
                    return Landmark.RADIO_TOWER;
                } else if (currentPlayer.getCoins() >= Landmark.AMUSEMENT_PARK.getCost() && !currentPlayer.hasAmusementPark()) {
                    return Landmark.AMUSEMENT_PARK;
                } else if (currentPlayer.getCoins() >= Landmark.SHOPPING_MALL.getCost() && !currentPlayer.hasShoppingMall()) {
                    return Landmark.SHOPPING_MALL;
                } else if (currentPlayer.getCoins() >= Landmark.TRAIN_STATION.getCost() && !currentPlayer.hasTrainStation()) {
                    return Landmark.TRAIN_STATION;
                }
                throw new InvalidMoveException("Not enough money to purchase a landmark.");
            }

            @Override
            public Card chooseCard() throws InvalidMoveException {
                List<Card> randomCards = Arrays.asList(Card.values());
                Collections.shuffle(randomCards);
                for (Card card : randomCards) {
                    if (gameStock.get(card) > 0 && currentPlayer.getCoins() >= card.getCost() &&
                            (card.getCategory() != CardCategory.PURPLE || currentPlayer.getStock().getCardCount(card)==0)) {
                        return card;
                    }
                }
                throw new InvalidMoveException("Cannot afford to purchase a card.");
            }
        }
}
