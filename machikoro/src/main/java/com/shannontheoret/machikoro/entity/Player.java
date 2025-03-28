package com.shannontheoret.machikoro.entity;

import com.shannontheoret.machikoro.*;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity(name="player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name="number", nullable = false)
    private int number;

    @Column(name="name", nullable = true)
    private String name;

    @ElementCollection
    @CollectionTable(
            name = "landmarks",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_landmark_player")
            )
    )
    @Column(name = "landmarks", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Landmark> landmarks = EnumSet.noneOf(Landmark.class);

    @ElementCollection
    @CollectionTable(
            name="stock",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_stock_player")))
    @MapKeyColumn(name="card", nullable = false)
    @Column(name="count", nullable = false)
    @Enumerated(EnumType.STRING)
    private Map<Card, Integer> stock = new EnumMap<>(Card.class);

    @Column(name="coins", nullable = false)
    private int coins = GameRules.STARTING_COINS;

    @Column(name="npc", nullable = false)
    private boolean npc = false;

    @Column(name="strategy", nullable = true)
    private Strategy strategy;

    public Player(Integer number) throws GameMechanicException {
        if (number > 4) {
            throw new GameMechanicException("Cannot have a player number greater than 4.");
        }
        this.number = number;
        addCard(Card.WHEAT);
        addCard(Card.BAKERY);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isNpc() {
        return npc;
    }

    public void setNpc(boolean npc) {
        this.npc = npc;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public boolean hasTrainStation() {
        return landmarks.contains(Landmark.TRAIN_STATION);
    }

    public boolean hasShoppingMall() {
        return landmarks.contains(Landmark.SHOPPING_MALL);
    }

    public boolean hasAmusementPark() {
        return landmarks.contains(Landmark.AMUSEMENT_PARK);
    }

    public boolean hasRadioTower() {
        return landmarks.contains(Landmark.RADIO_TOWER);
    }

    public Set<Landmark> getLandmarks() {
        return landmarks;
    }

    public Map<Card, Integer> getStock() {
        return stock;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean hasWon() {
        return (landmarks.size() == 4);
    }

    public void increaseCoinCount(int amountToIncreaseBy) {
        coins += amountToIncreaseBy;
    }

    public void decreaseCoinCount(int amountToDecreaseBy) throws GameMechanicException {
        if (coins < amountToDecreaseBy) {
            throw new GameMechanicException("Cannot have a negative coin balance.");
        }
        coins -= amountToDecreaseBy;
    }

    public Boolean hasLandmark(Landmark landmark) {
        return landmarks.contains(landmark);
    }

    public void purchaseLandmark(Landmark landmark) throws GameMechanicException {
        decreaseCoinCount(landmark.getCost());
        if (landmarks.contains(landmark)) {
            throw new GameMechanicException("Cannot purchase the same landmark twice.");
        }
        landmarks.add(landmark);
    }

    public void addCard(Card card) {
        if (!stock.containsKey(card)) {
            stock.put(card, 1);
        } else {
            stock.put(card, stock.get(card) + 1);
        }
    }

    public void purchaseCard(Card card) throws GameMechanicException {
        addCard(card);
        decreaseCoinCount(card.getCost());
    }

    public Integer getCountOfCardsInCategory(CardCategory category) {
        int countOfCategoryCards = 0;
        List<Card> categoryCards = stock.keySet()
                .stream()
                .filter(card -> card.getCategory() == category)
                .collect(Collectors.toList());
        for ( Card cardCategory: categoryCards) {
            countOfCategoryCards += stock.get(cardCategory);
        }
        return countOfCategoryCards;
    }

    public Set<Card> getRedCardsForRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isSteals())
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Set<Card> getBlueCardsForRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnAnyonesTurn())
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Set<Card> getCardsForPlayerRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnPlayersTurn() & card.getCategory() != CardCategory.PURPLE)
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Integer getCardCount(Card card) {
        Integer count = 0;
        if (stock.containsKey(card)) {
            count = stock.get(card);
        }
        return count;
    }

    public Map<Integer, Integer> getStockMap() {
        Map<Integer, Integer> stockMap = new HashMap<>();
        for (Map.Entry<Card, Integer> entry : stock.entrySet()) {
            stockMap.put(entry.getKey().ordinal(), entry.getValue());
        }
        return stockMap;
    }

    public Integer getNumberOfCards() {
        Integer count = 0;
        for (Map.Entry<Card, Integer> entry: stock.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    public Integer getNumberOfCardsOverSix() {
        Integer count = 0;
        for (Map.Entry<Card, Integer> entry: stock.entrySet()) {
            for (Integer roll : entry.getKey().getRolls()) {
                if (roll > 6) {
                    count += entry.getValue();
                    break;
                }
            }
        }
        return count;
    }

}
