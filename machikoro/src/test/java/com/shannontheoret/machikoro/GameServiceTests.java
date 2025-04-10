package com.shannontheoret.machikoro;

import com.shannontheoret.machikoro.dao.GameDao;
import com.shannontheoret.machikoro.dao.PlayerDao;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameCodeNotFoundException;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import com.shannontheoret.machikoro.exception.InvalidMoveException;
import com.shannontheoret.machikoro.service.GameService;
import com.shannontheoret.machikoro.utilities.GameUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

    @Mock
    GameDao gameDao;

    @Mock
    PlayerDao playerDao;

    @Mock
    GameUtilities gameUtilities;

    @InjectMocks
    GameService gameService;

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void newGame_valid(Integer numberOfPlayers) throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(numberOfPlayers);

        assertEquals("testCode", game.getCode(), "Game code should be 'testCode'");
        assertEquals(numberOfPlayers, game.getPlayers().size(), "Game should have " + numberOfPlayers + " players.");
        assertEquals(Step.SETUP, game.getStep(), "Game step should be SETUP");
        assertEquals(Card.values().length, game.getGameStock().size(), "Game stock should contain all card types");
        for (Card card : Card.values()) {
            if (card.getCategory() == CardCategory.PURPLE) {
                assertEquals(numberOfPlayers, game.getGameStock().get(card), "Purple cards should have a stock of " + numberOfPlayers);
            } else {
                assertEquals(8, game.getGameStock().get(card), "Non-purple cards should have a stock of 8");
            }
        }
        for (Player player : game.getPlayers()) {
            for (Landmark landmark : Landmark.values()) {
                assertFalse(player.hasLandmark(landmark), "Player should not have landmark " + landmark);
            }
            assertEquals(3, player.getCoins(), "Player should have 3 coins.");
            assertEquals(1, player.getCardCount(Card.WHEAT), "Player should have 1 wheat card.");
            assertEquals(1, player.getCardCount(Card.BAKERY), "Player should have 1 bakery card.");
        }

        verifySaves(game, 1);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5})
    public void newGame_invalid(Integer numberOfPlayers) {
        when(gameUtilities.generateCode()).thenReturn("testCode");

        assertThrows(IllegalArgumentException.class, () -> gameService.newGame(numberOfPlayers), "Should throw IllegalArgumentException");

        verify(gameDao, times(0)).save(any(Game.class));
        verify(playerDao, times(0)).save(any(Player.class));
    }

    @Test
    public void setupPlayer_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.setupPlayer("testCode", 1, "Sarah", false);

        assertEquals("Sarah", game.findPlayerByNumber(1).getName(), "Player 1 should be named 'Sarah'");
        assertEquals("Player 2", game.findPlayerByNumber(2).getName(), "Player 2 should be named 'Player 2'");
        assertEquals(Step.SETUP, game.getStep(), "Game step should be SETUP");

        verifySaves(game, 2);
    }

    @Test
    public void setupPlayer_playerDoesNotExist_throwsGameMechanicException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(GameMechanicException.class, () -> gameService.setupPlayer("testCode", 3, "Sarah", false));

        verifySaves(game, 1);
    }

    @Test
    public void setupPlayer_wrongStep_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        game.setStep(Step.ROLL);

        assertThrows(InvalidMoveException.class, () -> gameService.setupPlayer("testCode", 1, "Sarah", false));

        verifySaves(game, 1);
    }

    @Test
    public void beginGame_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);
        
        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.beginGame("testCode");
        
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be player 1");

        verifySaves(game, 2);
    }

    @Test
    public void beginGame_invalid() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(3);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        game.setStep(Step.ROLL);

        assertThrows(InvalidMoveException.class, () -> gameService.beginGame("testCode"));

        verifySaves(game, 1);
    }

    @ParameterizedTest
    @MethodSource("rollAndExpectedCoins")
    public void roll_valid(Integer currentPlayer, Integer rollValue, Integer player1Coins, Integer player2Coins, Integer player3Coins) throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        game.setStep(Step.ROLL);

        Player player1 = game.findPlayerByNumber(1);
        player1.getStock().putAll(Map.of(
                Card.RANCH, 3,
                Card.CONVENIENCE_STORE, 2,
                Card.FOREST, 1,
                Card.CHEESE_FACTORY, 4,
                Card.CAFE, 2,
                Card.MINE, 2,
                Card.FRUIT_AND_VEGETABLE_GARDEN, 5
        )); //also includes 1 wheat field and 1 bakery

        Player player2 = game.findPlayerByNumber(2);
        player2.getStock().putAll(Map.of(
                Card.CONVENIENCE_STORE, 2,
                Card.FOREST, 3,
                Card.APPLE_ORCHARD, 1,
                Card.CAFE, 1
        )); //also includes 1 wheat field and 1 bakery

        Player player3 = game.findPlayerByNumber(3);
        player3.getStock().putAll(Map.of(
                Card.WHEAT, 2,
                Card.CAFE, 1,
                Card.STADIUM, 1
        ));

        player1.getLandmarks().add(Landmark.TRAIN_STATION);
        player2.getLandmarks().add(Landmark.TRAIN_STATION);
        player3.getLandmarks().add(Landmark.TRAIN_STATION);
        player3.getLandmarks().add(Landmark.SHOPPING_MALL);


        player1.setCoins(5);
        player2.setCoins(0);
        player3.setCoins(3);

        game.setCurrentPlayerNumber(currentPlayer);

        Boolean rollTwo = rollValue > 6;

        when(gameDao.findByCode("testCode")).thenReturn(game);

        if (rollTwo) {
            when(gameUtilities.generateRandomDieRoll()).thenReturn(6, rollValue - 6);
        } else {
            when(gameUtilities.generateRandomDieRoll()).thenReturn(rollValue);
        }

        gameService.roll("testCode", rollTwo);

        assertEquals(Step.BUY, game.getStep());
        assertEquals(player1Coins, player1.getCoins(), "The number of coins for player 1 should equal " + player1.getCoins());
        assertEquals(player2Coins, player2.getCoins(), "The number of coins for player 2 should equal " + player2Coins);
        assertEquals(player3Coins, player3.getCoins(), "The number of coins for player 3 should equal " + player3Coins);

        verifySaves(game, 2);
    }

    @Test
    public void roll_withRadioTower() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.ROLL);
        game.setCurrentPlayerNumber(1);
        Player player1 = game.findPlayerByNumber(1);
        player1.getStock().putAll(Map.of(Card.RANCH, 2, Card.STADIUM, 2));
        player1.setCoins(4);
        player1.getLandmarks().add(Landmark.RADIO_TOWER);

        when(gameUtilities.generateRandomDieRoll()).thenReturn(2);
        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.roll("testCode", false);

        assertEquals(Step.CONFIRM_ROLL, game.getStep(), "Game step should be CONFIRM_ROLL");
        assertEquals(4, player1.getCoins(), "Player should still have only 4 coins");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should still be 1.");
        assertTrue(game.getRolledOnce(), "Rolled once should be true");

        verifySaves(game, 2);
    }

    @Test
    public void roll_secondRollWithRadioTower() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.CONFIRM_ROLL);
        game.setCurrentPlayerNumber(1);
        game.setRolledOnce(true);
        Player player1 = game.findPlayerByNumber(1);
        player1.getStock().putAll(Map.of(
                Card.FOREST, 2,
                Card.MINE, 3,
                Card.FURNITURE_FACTORY, 2
        ));
        player1.setCoins(4);
        player1.getLandmarks().add(Landmark.RADIO_TOWER);
        player1.getLandmarks().add(Landmark.TRAIN_STATION);

        when(gameUtilities.generateRandomDieRoll()).thenReturn(3, 5); //total 8 to activate furniture factory
        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.roll("testCode", true);

        assertEquals(Step.BUY, game.getStep(), "Game step should be BUY");
        assertEquals(34, player1.getCoins(), "Player should have 34 coins"); //4 + (2 + 3) * 2 * 3
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should still be 1.");

        verifySaves(game, 2);
    }

    @Test
    public void roll_wrongStep_throwsInvalidMoveException() throws InvalidMoveException, GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(4);

        game.setStep(Step.STEAL);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.roll("testCode", false));

        verifySaves(game, 1);
    }

    @Test
    public void rollTwo_noTrainStation_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.ROLL);
        game.setCurrentPlayerNumber(2);
        game.findPlayerByNumber(1).getLandmarks().add(Landmark.TRAIN_STATION); // not current player
        game.findPlayerByNumber(2);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.roll("testCode", true));

        verifySaves(game, 1);
    }

    @Test
    public void confirmRoll_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.CONFIRM_ROLL);
        game.setCurrentPlayerNumber(2);
        Player player2 = game.findPlayerByNumber(2);
        player2.setCoins(2);
        player2.getStock().put(Card.CONVENIENCE_STORE, 3);
        player2.getLandmarks().add(Landmark.RADIO_TOWER);
        game.setDie1(4);
        game.setDie2(0);
        game.setRolledOnce(true);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.confirmRoll("testCode");

        assertEquals(Step.BUY, game.getStep(), "Game step is BUY");
        assertEquals(11, player2.getCoins(), "Player should have 11 coins");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player should be player 2");

        verifySaves(game, 2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RANCH", "CAFE", "CONVENIENCE_STORE", "FOREST", "CHEESE_FACTORY", "FURNITURE_FACTORY", "MINE", "FAMILY_RESTAURANT", "APPLE_ORCHARD", "FRUIT_AND_VEGETABLE_GARDEN"})
    public void purchaseCard_firstPurchase_valid(Card card) throws GameMechanicException, InvalidMoveException, GameCodeNotFoundException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        Player player2 = game.findPlayerByNumber(2);
        player2.setCoins(20);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseCard("testCode", card);

        Integer expectedCost = 20 - card.getCost();

        assertEquals(1, player2.getCardCount(card), "Player 2 should have 1 " + card);
        assertEquals(7, game.getGameStock().get(card), "Game stock should have 7 of " + card);
        assertEquals(expectedCost, player2.getCoins(), "Player 2 should have " + expectedCost + " coins");
        assertTrue(player2.getLandmarks().isEmpty(), "Player 2 should not have any landmarks");
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(3, game.getCurrentPlayerNumber(), "Current player should be player 3");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseCard_subsequentPurchase_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findPlayerByNumber(1);
        player.setCoins(4);
        game.getGameStock().put(Card.RANCH, 3);
        player.getStock().putAll(Map.of(Card.RANCH, 2, Card.FOREST, 1));

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseCard("testCode", Card.RANCH);

        assertEquals(3, player.getCardCount(Card.RANCH), "Player should have 3 ranch cards");
        assertEquals(2, game.getGameStock().get(Card.RANCH), "Game stock should have 2 ranch cards");
        assertEquals(3, player.getCoins(), "Player 1 should have 3 coins");
        assertTrue(player.getLandmarks().isEmpty(), "Player 1 should not have any landmarks");
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player should be player 2");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseCard_purchasePurple_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findPlayerByNumber(1);
        player.setCoins(8);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseCard("testCode", Card.TV_STATION);

        assertEquals(1, player.getCardCount(Card.TV_STATION), "Player should have 1 TV station card");
        assertEquals(2, game.getGameStock().get(Card.TV_STATION), "Game stock should have 2 TV station cards");
        assertEquals(1, player.getCoins(), "Player 1 should have 1 coin");
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player should be player 2");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseCard_afterDoublesWithAmusementPark_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        game.setDie1(2);
        game.setDie2(2);
        Player player = game.findPlayerByNumber(2);
        player.setCoins(5);
        player.getLandmarks().addAll(Set.of(Landmark.TRAIN_STATION, Landmark.AMUSEMENT_PARK));

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseCard("testCode", Card.CAFE);

        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player should still be player 2");
        assertEquals(3, player.getCoins(), "Player 2 should have 3 coins");
        assertEquals(1, player.getCardCount(Card.CAFE), "Player 2 should have 1 cafe card");
        assertEquals(7, game.getGameStock().get(Card.CAFE), "Game stock should have 7 cafe cards");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseCard_wrongStep_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.CONFIRM_ROLL);
        game.setCurrentPlayerNumber(2);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseCard("testCode", Card.CHEESE_FACTORY));

        verifySaves(game, 1);
    }

    @Test
    public void purchaseCard_notEnoughMoney_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(4);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(4);
        Player player = game.findPlayerByNumber(4);
        player.setCoins(1);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseCard("testCode", Card.FOREST));

        verifySaves(game, 1);
    }

    @Test
    public void purchaseCard_notEnoughStock_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        game.getGameStock().put(Card.CHEESE_FACTORY, 0);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseCard("testCode", Card.CHEESE_FACTORY));

        verifySaves(game, 1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"TV_STATION", "STADIUM"})
    public void purchaseCard_purpleCardAlreadyPurchased_throwsInvalidMoveException(Card card) throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);
        game.getGameStock().put(card, 2);
        player.getStock().put(card, 1);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseCard("testCode", card));

        verifySaves(game, 1);
    }

    @Test
    public void purchaseLandmark_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        Player player = game.findCurrentPlayer();
        player.setCoins(6);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseLandmark("testCode", Landmark.TRAIN_STATION);

        assertTrue(player.hasLandmark(Landmark.TRAIN_STATION), "Player should have Train Station");
        assertEquals(1, player.getLandmarks().size(), "Player should have 1 landmark");
        assertEquals(2, player.getCoins(), "Player should have 2 coins");
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be player 1");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseLandmark_subsequentLandmark_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        Player player = game.findCurrentPlayer();
        player.setCoins(14);
        player.getLandmarks().add(Landmark.TRAIN_STATION);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseLandmark("testCode", Landmark.SHOPPING_MALL);

        assertTrue(player.hasLandmark(Landmark.SHOPPING_MALL), "Player should have Shopping Mall");
        assertEquals(Set.of(Landmark.TRAIN_STATION, Landmark.SHOPPING_MALL), player.getLandmarks(), "Player should have Train Station and Shopping Mall");
        assertEquals(4, player.getCoins(), "Player should have 4 coins");
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(3, game.getCurrentPlayerNumber(), "Current player should be player 3");

        verifySaves(game, 2);
    }

    @Test
    public void purchaseLandmark_finalLandmark_gameEnd() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findCurrentPlayer();
        player.setCoins(24);
        player.getLandmarks().addAll(Set.of(Landmark.TRAIN_STATION, Landmark.SHOPPING_MALL, Landmark.AMUSEMENT_PARK));

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.purchaseLandmark("testCode", Landmark.RADIO_TOWER);

        assertEquals(2, player.getCoins(), "Player should have 2 coins");
        assertTrue(player.hasLandmark(Landmark.RADIO_TOWER), "Player should have Radio Tower");
        assertEquals(Step.WON, game.getStep(), "Game step should be WON");
        assertTrue(player.hasWon(), "Player should have won");
    }

    @Test
    public void purchaseLandmark_wrongStep_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2);

        game.setStep(Step.ROLL);
        game.setCurrentPlayerNumber(2);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseLandmark("testCode", Landmark.TRAIN_STATION));

        verifySaves(game, 1);
    }

    @Test
    public void purchaseLandmark_notEnoughMoney_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(4);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(2);
        Player player = game.findCurrentPlayer();
        player.setCoins(3);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseLandmark("testCode", Landmark.TRAIN_STATION));

        verifySaves(game, 1);
    }

    @Test
    public void purchaseLandmark_alreadyPurchased_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(3);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);
        player.getLandmarks().add(Landmark.TRAIN_STATION);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseLandmark("testCode", Landmark.TRAIN_STATION));

        verifySaves(game, 1);
    }

    @Test
    public void steal_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);
        game.setStep(Step.STEAL);
        game.setDie1(6);
        game.setCurrentPlayerNumber(1);
        Player player1 = game.findPlayerByNumber(1);
        player1.getStock().put(Card.TV_STATION, 1);
        player1.setCoins(3);
        game.findPlayerByNumber(2).setCoins(3);
        game.findPlayerByNumber(3).setCoins(7);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.steal("testCode", 3);

        assertEquals(Step.BUY, game.getStep(), "Game step should be BUY");
        assertEquals(8, player1.getCoins(), "Player 1 should have 8 coins");
        assertEquals(3, game.findPlayerByNumber(2).getCoins(), "Player 2 should have 3 coins.");
        assertEquals(2, game.findPlayerByNumber(3).getCoins(), "Player 3 should have 2 coins");

        verifySaves(game, 2);
    }

    @Test
    public void stealPartial_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);
        game.setStep(Step.STEAL);
        game.setDie1(6);
        game.setCurrentPlayerNumber(1);
        Player player1 = game.findPlayerByNumber(1);
        player1.getStock().put(Card.TV_STATION, 1);
        player1.setCoins(3);
        game.findPlayerByNumber(2).setCoins(3);
        game.findPlayerByNumber(3).setCoins(2);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.steal("testCode", 2);

        assertEquals(Step.BUY, game.getStep(), "Game step should be BUY");
        assertEquals(6, player1.getCoins(), "Player 1 should have 6 coins");
        assertEquals(0, game.findPlayerByNumber(2).getCoins(), "Player 2 should have 0 coins.");
        assertEquals(2, game.findPlayerByNumber(3).getCoins(), "Player 3 should have 2 coins");

        verifySaves(game, 2);
    }

    @Test
    public void completeTurn_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3);
        game.setStep(Step.BUY);
        game.setDie1(2);
        game.setCurrentPlayerNumber(3);
        Player player3 = game.findPlayerByNumber(3);
        player3.setCoins(4);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.completeTurn("testCode");

        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1");
        assertEquals(4, player3.getCoins(), "Player 3 coins should be 4");

        verifySaves(game, 2);
    }

    private void setupMocks() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        when(gameUtilities.generateCode()).thenReturn("testCode");
    }

    private void verifySaves(Game game, Integer count) {
        verify(gameDao, times(count)).save(game);
        for (Player player : game.getPlayers()) {
            verify(playerDao, times(count)).save(player);
        }
    }

    private static Stream<Arguments> rollAndExpectedCoins() {
        return Stream.of(
                Arguments.of(1, 1, 6, 1, 5),
                Arguments.of(1, 2, 9, 0, 3),
                Arguments.of(1, 3, 3, 1, 5),
                Arguments.of(1, 4, 11, 0, 3),
                Arguments.of(1, 5, 6, 3, 3),
                Arguments.of(1, 6, 5, 0, 3),
                Arguments.of(1, 7, 41, 0, 3),
                Arguments.of(1, 8, 5, 0, 3),
                Arguments.of(1, 9, 15, 0, 3),
                Arguments.of(1, 10, 5, 3, 3),
                Arguments.of(1, 11, 15, 0, 3),
                Arguments.of(1, 12, 15, 0, 3),

                Arguments.of(2, 1, 6, 1, 5),
                Arguments.of(2, 2, 8, 1, 3),
                Arguments.of(2, 3, 5, 1, 3),
                Arguments.of(2, 4, 5, 6, 3),
                Arguments.of(2, 5, 6, 3, 3),
                Arguments.of(2, 6, 5, 0, 3),
                Arguments.of(2, 7, 5, 0, 3),
                Arguments.of(2, 8, 5, 0, 3),
                Arguments.of(2, 9, 15, 0, 3),
                Arguments.of(2, 10, 5, 3, 3),
                Arguments.of(2, 11, 5, 0, 3),
                Arguments.of(2, 12, 5, 0, 3),

                Arguments.of(3, 1, 6, 1, 5),
                Arguments.of(3, 2, 8, 0, 5),
                Arguments.of(3, 3, 7, 1, 2),
                Arguments.of(3, 4, 5, 0, 3),
                Arguments.of(3, 5, 6, 3, 3),
                Arguments.of(3, 6, 3, 0, 5),
                Arguments.of(3, 7, 5, 0, 3),
                Arguments.of(3, 8, 5, 0, 3),
                Arguments.of(3, 9, 15, 0, 3),
                Arguments.of(3, 10, 5, 3, 3),
                Arguments.of(3, 11, 5, 0, 3),
                Arguments.of(3, 12, 5, 0, 3)
        );
    }
}