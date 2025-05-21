package com.shannontheoret.machikoro.entity;

import com.shannontheoret.machikoro.*;
import com.shannontheoret.machikoro.dto.PlayerDTO;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import jakarta.persistence.*;

import java.util.*;

@Entity(name="player")
public class Player {
    private static final double COINS_TOWARDS_PROGRESS_FACTOR = 0.5;

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
    @OrderBy("card ASC")
    private Map<Card, Integer> stock = new EnumMap<>(Card.class);

    @Column(name="coins", nullable = false)
    private int coins = GameRules.STARTING_COINS;

    @Column(name="npc", nullable = false)
    private boolean npc = false;

    @ElementCollection
    @CollectionTable(
            name="strategy",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_strategy_player")))
    @MapKeyColumn(name="strategy_name", nullable = true)
    @Column(name="degree", nullable = true)
    @Enumerated(EnumType.STRING)
    private Map<StrategyName, Integer> strategy;

    public Player() {
        addStartingCards();
    }

    public Player(Integer number) throws GameMechanicException {
        if (number > 4) {
            throw new GameMechanicException("Cannot have a player number greater than 4.");
        }
        this.number = number;
        addStartingCards();
    }

    public Player(PlayerDTO playerDTO) {
        this.number = playerDTO.getPlayerNumber();
        this.name = playerDTO.getPlayerName();
        this.npc = playerDTO.getIsNPC();
        if (npc) {
            this.strategy = playerDTO.getStrategy();
        }
        addStartingCards();
    }

    public void updateFromDTO(PlayerDTO playerDTO) {
        this.number = playerDTO.getPlayerNumber();
        if (playerDTO.getPlayerName().length() > 0) {
            this.name = playerDTO.getPlayerName();
        }
        this.npc = playerDTO.getIsNPC();
        if (npc) {
            this.strategy = playerDTO.getStrategy();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Map<StrategyName, Integer> getStrategy() {
        return strategy;
    }

    public void setStrategy(Map<StrategyName, Integer> strategy) {
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

    public double getProgress() {
        Integer totalValuePurchases = 0;
        for (Landmark purchasedLandmark : landmarks) {
            totalValuePurchases += purchasedLandmark.getCost();
        }
        Integer coinsTowardProgress = Math.min(Landmark.TOTAL_COST - totalValuePurchases, coins);
        return ((double) totalValuePurchases + (double) coinsTowardProgress * COINS_TOWARDS_PROGRESS_FACTOR)/((double) Landmark.TOTAL_COST);
    }

    public Player deepCopy() {
        Player copy = new Player();

        copy.setNumber(this.number);
        copy.setName(this.name);
        copy.setCoins(this.coins);
        copy.setNpc(this.npc);
        if (this.strategy != null && !this.strategy.isEmpty()) {
            copy.strategy = new EnumMap<>(this.strategy);
        }
        copy.landmarks = this.landmarks.isEmpty()
                ? EnumSet.noneOf(Landmark.class)
                : EnumSet.copyOf(this.landmarks);

        copy.stock = new EnumMap<>(this.stock);

        return copy;
    }

    private void addStartingCards() {
        addCard(Card.WHEAT);
        addCard(Card.BAKERY);
    }
}
