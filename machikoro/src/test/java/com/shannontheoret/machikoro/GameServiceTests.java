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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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

        Game game = gameService.newGame(numberOfPlayers, 0);

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

        assertThrows(IllegalArgumentException.class, () -> gameService.newGame(numberOfPlayers, 0), "Should throw IllegalArgumentException");

        verify(gameDao, times(0)).save(any(Game.class));
        verify(playerDao, times(0)).save(any(Player.class));
    }

    @Test
    public void namePlayer_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(2, 0);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.namePlayer("testCode", 1, "Sarah");

        assertEquals("Sarah", game.findPlayerByNumber(1).getName(), "Player 1 should be named 'Sarah'");
        assertEquals("Player 2", game.findPlayerByNumber(2).getName(), "Player 2 should be named 'Player 2'");
        assertEquals(Step.SETUP, game.getStep(), "Game step should be SETUP");

        verifySaves(game, 2);
    }

    @Test
    public void namePlayer_playerDoesNotExist_throwsGameMechanicException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2, 0);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(GameMechanicException.class, () -> gameService.namePlayer("testCode", 3, "Sarah"));

        verifySaves(game, 1);
    }

    @Test
    public void namePlayer_wrongStep_throwsInvalidMoveException() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(2, 0);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        game.setStep(Step.ROLL);

        assertThrows(InvalidMoveException.class, () -> gameService.namePlayer("testCode", 1, "Sarah"));

        verifySaves(game, 1);
    }

    @Test
    public void beginGame_valid() throws GameMechanicException, GameCodeNotFoundException, InvalidMoveException {
        setupMocks();

        Game game = gameService.newGame(3, 0);
        
        when(gameDao.findByCode("testCode")).thenReturn(game);

        gameService.beginGame("testCode");
        
        assertEquals(Step.ROLL, game.getStep(), "Game step should be ROLL");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be player 1");

        verifySaves(game, 2);
    }

    @Test
    public void beginGame_invalid() throws GameMechanicException {
        setupMocks();

        Game game = gameService.newGame(3, 0);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        game.setStep(Step.ROLL);

        assertThrows(InvalidMoveException.class, () -> gameService.beginGame("testCode"));

        verifySaves(game, 1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RANCH", "CAFE", "CONVENIENCE_STORE", "FOREST", "CHEESE_FACTORY", "FURNITURE_FACTORY", "MINE", "FAMILY_RESTAURANT", "APPLE_ORCHARD", "FRUIT_AND_VEGETABLE_GARDEN"})
    public void purchaseCard_firstPurchase_valid(Card card) throws GameMechanicException, InvalidMoveException, GameCodeNotFoundException {
        setupMocks();

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(2, 0);

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

        Game game = gameService.newGame(4, 0);

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

        Game game = gameService.newGame(2, 0);

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

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(2, 0);

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

        Game game = gameService.newGame(3, 0);

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

        Game game = gameService.newGame(2, 0);

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

        Game game = gameService.newGame(2, 0);

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

        Game game = gameService.newGame(4, 0);

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

        Game game = gameService.newGame(3, 0);

        game.setStep(Step.BUY);
        game.setCurrentPlayerNumber(1);
        Player player = game.findCurrentPlayer();
        player.setCoins(10);
        player.getLandmarks().add(Landmark.TRAIN_STATION);

        when(gameDao.findByCode("testCode")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.purchaseLandmark("testCode", Landmark.TRAIN_STATION));

        verifySaves(game, 1);
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

    //TODO: Purchases landmark tests
    //TODO: Roll tests

/*
    @Test
    public void rollSingle_valid() throws GameMechanicException {
        Game game = new Game();
        game.getPlayer1().getStock().addCard(Card.RANCH, 3);
        game.getPlayer1().getStock().addCard(Card.CONVENIENCE_STORE, 2);
        game.getPlayer1().getStock().addCard(Card.FOREST, 1);
        game.getPlayer1().getStock().addCard(Card.CHEESE_FACTORY, 4);
        game.getPlayer1().getStock().addCard(Card.CAFE, 2);
        game.getPlayer1().getStock().addCard(Card.MINE, 2);
        game.getPlayer1().getStock().addCard(Card.FRUIT_AND_VEGETABLE_GARDEN, 5);

        game.getPlayer2().getStock().addCard(Card.CONVENIENCE_STORE, 2);
        game.getPlayer2().getStock().addCard(Card.FOREST, 3);
        game.getPlayer2().getStock().addCard(Card.APPLE_ORCHARD);
        game.getPlayer2().getStock().addCard(Card.CAFE);

        game.getPlayer3().getStock().addCard(Card.WHEAT);
        game.getPlayer3().getStock().addCard(Card.CAFE, 1);
        game.getPlayer3().getStock().addCard(Card.STADIUM, 1);
        game.getPlayer3().setHasShoppingMall(true);

        game.getPlayer1().setCoins(5);
        game.getPlayer2().setCoins(0);
        game.getPlayer3().setCoins(3);

        game.handleRoll(1);
        assert game.getPlayer1().getCoins() == 6;
        assert game.getPlayer2().getCoins() == 1;
        assert game.getPlayer3().getCoins() == 5;

        game.handleRoll(2);
        assert game.getPlayer1().getCoins() == 10;
        assert game.getPlayer2().getCoins() == 1;
        assert game.getPlayer3().getCoins() == 5;

        game.handleRoll(3);
        assert game.getPlayer1().getCoins() == 8;
        assert game.getPlayer2().getCoins() == 2;
        assert game.getPlayer3().getCoins() == 7;

        game.getPlayer1().setCoins(0);

        game.handleRoll(3);
        assert game.getPlayer1().getCoins() == 1;
        assert game.getPlayer2().getCoins() == 2;
        assert game.getPlayer3().getCoins() == 7;

        game.setCurrentPlayer(game.getPlayer3());

        game.getPlayer2().setCoins(5);

        game.handleRoll(6);
        assert game.getPlayer1().getCoins() == 0;
        assert game.getPlayer2().getCoins() == 3;
        assert game.getPlayer3().getCoins() == 10;

        game.handleRoll(1);
        assert game.getPlayer1().getCoins() == 1;
        assert game.getPlayer2().getCoins() == 4;
        assert game.getPlayer3().getCoins() == 12;

        game.handleRoll(2);
        assert game.getPlayer1().getCoins() == 4;
        assert game.getPlayer2().getCoins() == 4;
        assert game.getPlayer3().getCoins() == 14;
    }

}
 */
}