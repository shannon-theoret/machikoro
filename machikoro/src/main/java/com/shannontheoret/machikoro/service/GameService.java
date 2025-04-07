package com.shannontheoret.machikoro.service;

import com.shannontheoret.machikoro.*;
import com.shannontheoret.machikoro.dao.GameDao;
import com.shannontheoret.machikoro.dao.PlayerDao;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameCodeNotFoundException;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import com.shannontheoret.machikoro.exception.InvalidMoveException;
import com.shannontheoret.machikoro.utilities.GameUtilities;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private GameDao gameDao;
    private PlayerDao playerDao;
    private GameUtilities gameUtilities;

    @Autowired
    public GameService(GameDao gameDao, PlayerDao playerDao, GameUtilities gameUtilities) {
        this.gameDao = gameDao;
        this.playerDao = playerDao;
        this.gameUtilities = gameUtilities;
    }

    @Transactional
    public Game findByCode(String code)  throws GameCodeNotFoundException {
        Game game = gameDao.findByCode(code);
        if (game==null) {
            throw new GameCodeNotFoundException(code);
        }
        return game;
    }

    @Transactional
    public Game newGame(Integer numberOfPlayers) throws GameMechanicException {
        Game game = new Game();
        game.setCode(gameUtilities.generateCode());
        if (numberOfPlayers < GameRules.MIN_PLAYERS || numberOfPlayers > GameRules.MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " + GameRules.MIN_PLAYERS + " and " + GameRules.MAX_PLAYERS);
        }
        Set<Player> players = new HashSet<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            Player player = new Player();
            player.setNumber(i);
            player.setName("Player " + i);
            playerDao.save(player);
            players.add(player);
        }
        game.setPlayers(players);
        Map<Card, Integer> gameStock = game.getGameStock();
        for (Card card : Card.values()) {
            if (card.getCategory() == CardCategory.PURPLE) {
                gameStock.put(card, numberOfPlayers);
            } else {
                gameStock.put(card, 8);
            }
        }
        game.setStep(Step.SETUP);
        //TODO: add npc
        save(game);
        return game;
    }

    @Transactional
    public Game setupPlayer(String code, Integer number, String name, Boolean isNPC) throws GameCodeNotFoundException, GameMechanicException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.SETUP) {
            throw new InvalidMoveException("Game has already begun");
        }
        Player player = game.findPlayerByNumber(number);
        if (name.length() > 0) {
            player.setName(name);
        }
        player.setNpc(isNPC);
        save(game);
        return game;
    }

    @Transactional
    public Game beginGame(String code) throws GameCodeNotFoundException, InvalidMoveException, GameMechanicException {
        Game game = findByCode(code);
        if (game.getStep() != Step.SETUP) {
            throw new InvalidMoveException("Game has already begun");
        }
        game.setStep(Step.ROLL);
        game.setCurrentPlayerNumber(1);
        save(game);
        return game;
    }

    @Transactional
    public Game roll(String code, Boolean rollTwo) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.ROLL && game.getStep() != Step.CONFIRM_ROLL) {
            throw new InvalidMoveException("Cannot roll dice at this time.");
        }
        Player currentPlayer = game.findCurrentPlayer();
        game.setDie1(gameUtilities.generateRandomDieRoll());
        if (rollTwo) {
            if(!currentPlayer.hasTrainStation()) {
                throw new InvalidMoveException("Player cannot roll two dice because player does not have a train station.");
            }
            game.setDie2(gameUtilities.generateRandomDieRoll());
        } else {
            game.setDie2(0);
        }
        if (currentPlayer.hasRadioTower() && !game.getRolledOnce()) {
            game.setRolledOnce(true);
            game.setStep(Step.CONFIRM_ROLL);
        } else {
            acceptRoll(game);
        }
        save(game);
        return game;
    }

    @Transactional
    public Game confirmRoll(String code) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.CONFIRM_ROLL) {
            throw new InvalidMoveException("Cannot confirm roll at this time.");
        }
        acceptRoll(game);
        save(game);
        return game;
    }

    @Transactional
    public Game steal(String code, Integer playerNumberToStealFrom) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        Player currentPlayer = game.findCurrentPlayer();
        if (game.getStep() != Step.STEAL || currentPlayer.getCardCount(Card.TV_STATION) == 0) {
            throw new InvalidMoveException("Cannot steal at this time.");
        }
        Player playerToStealFrom = game.findPlayerByNumber(playerNumberToStealFrom);
        Integer amountToSteal = 0;
        if (playerToStealFrom.getCoins() < Card.TV_STATION.getAmountGained()) {
            amountToSteal = playerToStealFrom.getCoins();
        } else {
            amountToSteal = Card.TV_STATION.getAmountGained();
        }
        playerToStealFrom.decreaseCoinCount(amountToSteal);
        currentPlayer.increaseCoinCount(amountToSteal);
        game.setStep(Step.BUY);
        save(game);
        return game;
    }

    @Transactional
    public Game purchaseCard(String code, Card card) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.BUY) {
            throw new InvalidMoveException("Cannot purchase card at this time.");
        }
        if (game.getGameStock().get(card) == 0) {
            throw new InvalidMoveException("Card not in stock.");
        }
        Player currentPlayer = game.findCurrentPlayer();
        if (currentPlayer.getCoins() < card.getCost()) {
            throw new InvalidMoveException("Player does not have enough coins to purchase card.");
        }
        if (card.getCategory() == CardCategory.PURPLE && currentPlayer.getCardCount(card) == 1) {
            throw new InvalidMoveException("Player cannot purchase the same purple card twice.");
        }
        currentPlayer.purchaseCard(card);
        Integer stockQuantity = game.getGameStock().get(card);
        stockQuantity--;
        game.getGameStock().put(card, stockQuantity);
        endTurn(game);
        save(game);
        return game;
    }

    @Transactional
    public Game purchaseLandmark(String code, Landmark landmark) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.BUY) {
            throw new InvalidMoveException("Cannot purchase landmark at this time.");
        }
        Player currentPlayer = game.findCurrentPlayer();
        if (currentPlayer.getCoins() < landmark.getCost()) {
            throw new InvalidMoveException("Player does not have enough coins to purchase card.");
        }
        if (currentPlayer.hasLandmark(landmark)) {
            throw new InvalidMoveException("Player cannot purchase the same landmark twice.");
        }
        currentPlayer.purchaseLandmark(landmark);
        endTurn(game);
        save(game);
        return game;
    }

    @Transactional
    public Game completeTurn(String code) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.BUY) {
            throw new InvalidMoveException("Cannot complete turn at this time.");
        }
        endTurn(game);
        save(game);
        return game;
    }

    @Transactional
    public Game testStuff(String code) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        game.setDie1(3);
        game.setDie2(1);
        save(game);
        return game;
    }

    @Transactional
    private void save(Game game) {
        for (Player player : game.getPlayers()) {
            if (player.getId() == null) {
                playerDao.save(player);
            }
        }
        gameDao.save(game);
    }

    private void acceptRoll(Game game) throws GameMechanicException {
        handleRoll(game);
        if (game.getStep() != Step.STEAL) {
            game.setStep(Step.BUY);
        }
    }

    private void handleRoll(Game game) throws GameMechanicException {
        Integer roll = game.getDieTotal();
        // handle red cards first (other players steal from current player)
        Player currentPlayer = game.findCurrentPlayer();
        Player playerToSteal = game.findNextPlayer(game.getCurrentPlayerNumber());
        while (playerToSteal.getNumber() != game.getCurrentPlayerNumber()) {
            Set<Card> releventCards = playerToSteal.getRedCardsForRoll(roll);
            for (Card card: releventCards) {
                if (card.isBasic()) {
                    int amountGainedPerCard = card.getAmountGained();
                    if (playerToSteal.hasShoppingMall() && card.getCategory() == CardCategory.CUP) {
                        amountGainedPerCard++;
                    }
                    int amountToSteal = amountGainedPerCard * playerToSteal.getCardCount(card);
                    if (currentPlayer.getCoins() < amountToSteal) {
                        amountToSteal = currentPlayer.getCoins();
                    }
                    currentPlayer.decreaseCoinCount(amountToSteal);
                    playerToSteal.increaseCoinCount(amountToSteal);
                }
            }
            playerToSteal = game.findNextPlayer(playerToSteal.getNumber());
        }
        // handle green and blue cards for current character
        Set<Card> releventCards = currentPlayer.getCardsForPlayerRoll(roll);
        for (Card card : releventCards) {
            int amountToAdd = 0;
            if (card.isBasic()) {
                int amountGainedPerCard = card.getAmountGained();
                if(currentPlayer.hasShoppingMall() && card.getCategory() == CardCategory.STORE) {
                    amountGainedPerCard++;
                }
                amountToAdd = amountGainedPerCard * currentPlayer.getCardCount(card);
            } else if (card == Card.CHEESE_FACTORY) {
                int numberOfCows = currentPlayer.getCountOfCardsInCategory(CardCategory.COW);
                amountToAdd = numberOfCows * card.getAmountGained() * currentPlayer.getCardCount(card);
            } else if (card == Card.FURNITURE_FACTORY) {
                int countOfGearCards = currentPlayer.getCountOfCardsInCategory(CardCategory.GEAR);
                amountToAdd = countOfGearCards * card.getAmountGained() * currentPlayer.getCardCount(card);
            } else if (card == Card.FRUIT_AND_VEGETABLE_GARDEN) {
                int countOfGrainCards = currentPlayer.getCountOfCardsInCategory(CardCategory.GRAIN);
                amountToAdd = countOfGrainCards * card.getAmountGained() * currentPlayer.getCardCount(card);
            }
            currentPlayer.increaseCoinCount(amountToAdd);
        }

        //handle blue cards for other players
        Player otherPlayer = game.findNextPlayer(currentPlayer.getNumber());
        while(otherPlayer.getNumber() != currentPlayer.getNumber()) {
            releventCards = otherPlayer.getBlueCardsForRoll(roll);
            for (Card card : releventCards) {
                if (card.isBasic()) {
                    int amountToAdd = card.getAmountGained() * otherPlayer.getCardCount(card);
                    otherPlayer.increaseCoinCount(amountToAdd);
                }
            }
            otherPlayer = game.findNextPlayer(otherPlayer.getNumber());
        }

        //handle purple cards for current player
        if (currentPlayer.getCardCount(Card.STADIUM) != 0 && Card.STADIUM.rollApplies(roll)) {
            Player playerToStealFrom = game.findNextPlayer(currentPlayer.getNumber());
            while (!playerToStealFrom.equals(currentPlayer)) {
                int amountToSteal = 0;
                if (playerToStealFrom.getCoins() < Card.STADIUM.getAmountGained()) {
                    amountToSteal = playerToStealFrom.getCoins();
                } else {
                    amountToSteal = Card.STADIUM.getAmountGained();
                }
                playerToStealFrom.decreaseCoinCount(amountToSteal);
                currentPlayer.increaseCoinCount(amountToSteal);
                playerToStealFrom = game.findNextPlayer(playerToStealFrom.getNumber());
            }
        }
        if (currentPlayer.getCardCount(Card.TV_STATION) != 0 && Card.TV_STATION.rollApplies(roll)) {
            game.setStep(Step.STEAL);
        }
    }

    private void endTurn(Game game) throws GameMechanicException {
        if (game.findCurrentPlayer().hasWon()) {
            game.setStep(Step.WON);
        } else if (game.findCurrentPlayer().hasAmusementPark() && game.isDoubles()) {
            game.setStep(Step.ROLL);
        } else {
            game.incrementCurrentPlayerNumber();
            game.setStep(Step.ROLL);
            game.setRolledOnce(false);
            game.setDie1(0);
            game.setDie2(0);
        }
    }
}
