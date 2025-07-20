package com.shannontheoret.machikoro.service;

import com.shannontheoret.machikoro.*;
import com.shannontheoret.machikoro.dao.GameDao;
import com.shannontheoret.machikoro.dao.PlayerDao;
import com.shannontheoret.machikoro.dto.PlayerDTO;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameCodeNotFoundException;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import com.shannontheoret.machikoro.exception.InvalidMoveException;
import com.shannontheoret.machikoro.utilities.GameUtilities;
import com.shannontheoret.machikoro.utilities.RollEffectCalculator;
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
            players.add(player);
        }
        game.setPlayers(players);
        Map<Card, Integer> gameStock = game.getGameStock();
        for (Card card : Card.values()) {
            if (card.getCategory() == CardCategory.PURPLE) {
                gameStock.put(card, numberOfPlayers);
            } else {
                gameStock.put(card, GameRules.STARTING_STOCK);
            }
        }
        game.setStep(Step.SETUP);
        return game;
    }

    @Transactional
    public Game setupPlayer(String code, PlayerDTO playerDTO) throws GameCodeNotFoundException, GameMechanicException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != Step.SETUP) {
            throw new InvalidMoveException("Game has already begun");
        }
        Player player = game.getPlayerByNumber(playerDTO.getPlayerNumber());
        player.updateFromDTO(playerDTO);
        return game;
    }

    public Game setupPlayer(Game game, PlayerDTO playerDTO) throws GameCodeNotFoundException, GameMechanicException, InvalidMoveException {
            if (game.getStep() != Step.SETUP) {
                throw new InvalidMoveException("Game has already begun");
            }
            Player player = game.getPlayerByNumber(playerDTO.getPlayerNumber());
            player.updateFromDTO(playerDTO);
            return game;
    }

    @Transactional
    public Game beginGame(List<PlayerDTO> players) throws GameCodeNotFoundException, InvalidMoveException, GameMechanicException {
        Game game = newGame(players.size());
        for (PlayerDTO player : players) {
            setupPlayer(game, player);
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
        Player currentPlayer = game.getCurrentPlayer();
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
        Player currentPlayer = game.getCurrentPlayer();
        if (game.getStep() != Step.STEAL || currentPlayer.getStock().getOrDefault(Card.TV_STATION, 0) == 0) {
            throw new InvalidMoveException("Cannot steal at this time.");
        }
        Player playerToStealFrom = game.getPlayerByNumber(playerNumberToStealFrom);
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
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getCoins() < card.getCost()) {
            throw new InvalidMoveException("Player does not have enough coins to purchase card.");
        }
        if (card.getCategory() == CardCategory.PURPLE && currentPlayer.getStock().getOrDefault(card, 0) == 1) {
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
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getCoins() < landmark.getCost()) {
            throw new InvalidMoveException("Player does not have enough coins to purchase landmark.");
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
    public Game makeNPCMove(String code) throws GameCodeNotFoundException, InvalidMoveException, GameMechanicException {
        Game game = findByCode(code);
        if (!game.getCurrentPlayer().isNpc()) {
            throw new InvalidMoveException("Current player is not an NPC");
        }
        StrategicTurnDecisionEngine decisionEngine = new StrategicTurnDecisionEngine(game.getCurrentPlayer().getStrategy(), game);
        switch (game.getStep()) {
            case ROLL:
                 roll(code, !decisionEngine.rollSingleDice());
                break;
            case CONFIRM_ROLL:
                if (decisionEngine.reroll()) {
                    roll(code, !decisionEngine.rollSingleDice());
                } else {
                    confirmRoll(code);
                }
                break;
            case STEAL:
                steal(code, decisionEngine.choosePlayerToStealFrom());
                break;
            case BUY:
                BuyingDecision buyingDecision = decisionEngine.makeBuyingDecision();
                if (buyingDecision.isBuyingCard()) {
                    purchaseCard(code, buyingDecision.getCardToPurchase());
                } else if (buyingDecision.isBuyingLandmark()) {
                    purchaseLandmark(code, buyingDecision.getLandmarkToPurchase());
                } else {
                    return completeTurn(code);
                }
                break;
            case SETUP:
            case WON:
                throw new InvalidMoveException("Game step is " + game.getStep() + " and does not require an npc decision.");
            default:
                throw new IllegalStateException("Unexpected step: " + game.getStep());
        }
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
        Player currentPlayer = game.getCurrentPlayer();

        Map<Integer, Integer> playerRollEffects = RollEffectCalculator.calculateRedCardEffects(game, roll);
        applyPlayerEffects(game, playerRollEffects);

        currentPlayer.increaseCoinCount(RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, roll));

        playerRollEffects = RollEffectCalculator.calculateOtherPlayersBlueEffects(game, roll);
        applyPlayerEffects(game, playerRollEffects);

        playerRollEffects = RollEffectCalculator.calculateStadiumEffects(game, roll);
        applyPlayerEffects(game, playerRollEffects);

        if (currentPlayer.getStock().getOrDefault(Card.TV_STATION, 0) != 0 && Card.TV_STATION.rollApplies(roll)) {
            game.setStep(Step.STEAL);
        }
    }

    private void applyPlayerEffects(Game game, Map<Integer, Integer> playerRollEffects) throws GameMechanicException {
        for (Map.Entry<Integer, Integer> entry : playerRollEffects.entrySet()) {
            Player player = game.getPlayerByNumber(entry.getKey());
            int coinChange = entry.getValue();
            if (coinChange > 0) {
                player.increaseCoinCount(coinChange);
            } else {
                player.decreaseCoinCount(-coinChange);
            }
        }
    }

    private void endTurn(Game game) throws GameMechanicException {
        if (game.getCurrentPlayer().hasWon()) {
            game.setStep(Step.WON);
        } else if (game.getCurrentPlayer().hasAmusementPark() && game.isDoubles()) {
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
