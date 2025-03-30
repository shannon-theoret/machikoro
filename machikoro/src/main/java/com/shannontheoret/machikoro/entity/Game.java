package com.shannontheoret.machikoro.entity;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.Step;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import jakarta.persistence.*;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Game {
    @Id
    @Column(nullable = false, length = 8, updatable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Step step;

    @Column(name = "die1", nullable = true)
    private Integer die1 = 0;

    @Column(name = "die2", nullable = true)
    private Integer die2 = 0;

    @Column(name="rolled_once", nullable = false)
    private Boolean rolledOnce = false;

    @ElementCollection
    @CollectionTable(
            name="game_stock",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_game_stock_game")))
    @MapKeyColumn(name="card", nullable = false)
    @Column(name="count", nullable = false)
    @Enumerated(EnumType.STRING)
    @OrderBy("card ASC")
    private Map<Card, Integer> gameStock = new EnumMap<>(Card.class);

    @Column(name = "currentPlayerNumber", nullable = false)
    private Integer currentPlayerNumber = 1;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "game_code", referencedColumnName = "code")
    private Set<Player> players = new HashSet<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Integer getDie1() {
        return die1;
    }

    public void setDie1(Integer die1) throws GameMechanicException {
        confirmValidDieRoll(die1);
        this.die1 = die1;
    }

    public Integer getDie2() {
        return die2;
    }

    public void setDie2(Integer die2) throws GameMechanicException {
        confirmValidDieRoll(die2);
        this.die2 = die2;
    }

    public Integer getDieTotal() throws GameMechanicException {
        if (die1 == null) {
            throw new GameMechanicException("Die 1 has not been rolled.");
        }
        if (die2 == null) {
            return die1;
        } else {
            return die1 + die2;
        }
    }

    public Boolean isDoubles() {
        return (die1 != 0 && die1 == die2);
    }

    public Boolean getRolledOnce() {
        return rolledOnce;
    }

    public void setRolledOnce(Boolean rolledOnce) {
        this.rolledOnce = rolledOnce;
    }

    public Map<Card, Integer> getGameStock() {
        return gameStock;
    }

    public void setGameStock(Map<Card, Integer> gameStock) {
        this.gameStock = gameStock;
    }

    public Integer getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(Integer currentPlayerNumber) {
        this.currentPlayerNumber = currentPlayerNumber;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public void incrementCurrentPlayerNumber() throws GameMechanicException {
        currentPlayerNumber = findNextPlayerNumber(currentPlayerNumber);
    }

    public Player findCurrentPlayer() throws GameMechanicException {
        return findPlayerByNumber(currentPlayerNumber);
    }

    public Player findNextPlayer(Integer playerNumber) throws GameMechanicException {
        return findPlayerByNumber(findNextPlayerNumber(playerNumber));
    }

    public Player findPlayerByNumber(Integer playerNumber) throws GameMechanicException {
        for (Player player : players) {
            if (player.getNumber() == playerNumber) {
                return player;
            }
        }
        throw new GameMechanicException("Player not found.");
    }

    private Integer findNextPlayerNumber(Integer playerNumber) throws GameMechanicException {
        Integer nextPlayerNumber = playerNumber + 1;
        if (nextPlayerNumber > players.size()) {
            nextPlayerNumber = 1;
        }
        return nextPlayerNumber;
    }

    private void confirmValidDieRoll(Integer dieNumber) throws GameMechanicException {
        if (dieNumber < 0 || dieNumber > 6) { //die = 0 means die is not rolled
            throw new GameMechanicException("Die roll must be between 1 and 6");
        }
    }
}
