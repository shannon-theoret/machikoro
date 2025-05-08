package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.Landmark;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RollEffectCalculatorTests {

    private static Map<Integer, Integer> noPlayerEffects = Map.of(
            1,0,
            2,0,
            3,0,
            4,0);

    @Test
    public void calculateRedCardEffects_noEffectFromRoll() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateRedCardEffects(game,4));
    }

    @Test
    public void calculateRedCardEffects_noEffectFromPurple() throws GameMechanicException {
        Game game = createTestGame(4);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateRedCardEffects(game, 6));
    }

    @Test
    public void calculateRedCardEffects_coinsRunOut() throws GameMechanicException {
        Game game = createTestGame(4);
        Map<Integer, Integer> expectedOutcome = Map.of(
                1,1,
                2,0,
                3,1,
                4,-2
        );
        assertEquals(expectedOutcome, RollEffectCalculator.calculateRedCardEffects(game, 3));
    }

    @Test
    public void calculateRedCardEffects_shoppingMallEffect() throws GameMechanicException {
        Game game = createTestGame(3);
        Map<Integer, Integer> expectedOutcome = Map.of(
                1,4,
                2,0,
                3,-4,
                4,0
        );
        assertEquals(expectedOutcome, RollEffectCalculator.calculateRedCardEffects(game, 3));
    }

    @Test
    public void calculateRedCardEffects_familyRestaurant() throws GameMechanicException {
        Game game = createTestGame(3);
        Map<Integer, Integer> playerEffects = Map.of(
                1,3,
                2,0,
                3,-3,
                4,0
        );
        assertEquals(playerEffects, RollEffectCalculator.calculateRedCardEffects(game, 9));
    }

    @Test
    public void calculateRedCardEffects_familyRestaurantRunsOut() throws GameMechanicException {
        Game game = createTestGame(2);
        Map<Integer, Integer> expectedPlayerEffects = Map.of(
                1,1,
                2,-1,
                3,0,
                4,0
        );
        assertEquals(expectedPlayerEffects, RollEffectCalculator.calculateRedCardEffects(game,3));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_withShoppingMall() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(12, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 4));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_onlyBlue() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(1, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 1));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_blueAndGreen() throws GameMechanicException {
        Game game = createTestGame(2);
        assertEquals(3, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 2));
    }

    @Test
    public void  calculateGreenAndBlueEffectsForCurrentPlayer_cheeseFactory() throws GameMechanicException {
        Game game = createTestGame(2);
        assertEquals(12, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 7));
    }

    @Test
    public void  calculateGreenAndBlueEffectsForCurrentPlayer_uselessGreen() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(0, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 8));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_furnitureFactory() throws GameMechanicException {
       Game game = createTestGame(3);
       assertEquals(18, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 8));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_fruitAndVegetableGarden() throws GameMechanicException {
        Game game = createTestGame(2);
        assertEquals(18, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game,11));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_sharedNumberOnlyBlue() throws GameMechanicException {
        Game game = createTestGame(3);
        assertEquals(10, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 9));
    }

    @Test
    public void calculateGreenAndBlueEffectsForCurrentPlayer_noEffect() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(0, RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, 5));
    }

    @Test
    public void calculateOtherPlayersBlueEffects_noEffect() throws GameMechanicException {
        Game game = createTestGame(2);
        Map<Integer, Integer> expectedPlayerEffects = Map.of(
                1,0,
                3,0,
                4,0
        );
        assertEquals(expectedPlayerEffects, RollEffectCalculator.calculateOtherPlayersBlueEffects(game, 4));
    }

    @Test
    public void calculateOtherPlayersBlueEffects_valid() throws GameMechanicException {
        Game game = createTestGame(1);
        Map<Integer, Integer> expectedPlayerEffects = Map.of(
                2,0,
                3,1,
                4,3
        );
        assertEquals(expectedPlayerEffects, RollEffectCalculator.calculateOtherPlayersBlueEffects(game, 5));
    }

    @Test
    public void calculateStadiumEffects_noCard() throws GameMechanicException {
        Game game = createTestGame(1);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateStadiumEffects(game, 6));
    }

    @Test
    public void calculateStadiumEffects_rollDoesNotApply() throws GameMechanicException {
        Game game = createTestGame(4);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateStadiumEffects(game, 3));
    }

    @Test
    public void calculateStadiumEffects_valid() throws GameMechanicException {
        Game game = createTestGame(4);
        Map<Integer, Integer> expectedPlayerEffects = Map.of(
                1,-2,
                2,-1,
                3,-2,
                4,5
        );
        assertEquals(expectedPlayerEffects, RollEffectCalculator.calculateStadiumEffects(game, 6));
    }

    @Test
    public void calculateMaximumTVStationEffect_noCard() throws GameMechanicException {
        Game game = createTestGame(3);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateMaximumTVStationEffect(game, 6));
    }

    @Test
    public void calculateMaximumTVStationEffect_rollDoesNotApply() throws GameMechanicException {
        Game game = createTestGame(4);
        assertEquals(noPlayerEffects, RollEffectCalculator.calculateMaximumTVStationEffect(game, 2));
    }

    @Test
    public void calculateMaximumTVStationEffect_valid() throws GameMechanicException {
        Game game = createTestGame(4);
        Map<Integer, Integer> expectedPlayerEffects = Map.of(
                1,0,
                2, 0,
                3,-5,
                4,5
        );
        assertEquals(expectedPlayerEffects, RollEffectCalculator.calculateMaximumTVStationEffect(game, 6));
    }

    @Test
    public void calculateAllPossibleRollEffects_player1() throws GameMechanicException {
        Game game = createTestGame(1);
        Map<Integer, Map<Integer, Integer>> expectedAllRollEffects = new HashMap<>();
        expectedAllRollEffects.put(1,
                Map.of(
                    1,1,
                    2,1,
                    3,1,
                        4,2
                ));
        expectedAllRollEffects.put(2,
                Map.of(
                    1,4,
                       2,2,
                       3,0,
                       4,0
                ));
        expectedAllRollEffects.put(3,
                Map.of(
                        1,3,
                      2,0,
                      3,1,
                        4,0
                ));
        expectedAllRollEffects.put(4,
                Map.of(
                        1,12,
                        2,0,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(5,
                Map.of(
                        1,0,
                        2,0,
                        3,1,
                        4,3
                ));
        expectedAllRollEffects.put(6, noPlayerEffects);
        expectedAllRollEffects.put(7, noPlayerEffects);
        expectedAllRollEffects.put(8, noPlayerEffects);
        expectedAllRollEffects.put(9,
                Map.of(
                        1,-2,
                        2,0,
                        3,12,
                        4,0));
        expectedAllRollEffects.put(10,
                Map.of(
                        1,-2,
                        2,6,
                        3,2,
                        4,0
                ));
        expectedAllRollEffects.put(11, noPlayerEffects);
        expectedAllRollEffects.put(12, noPlayerEffects);
        assertEquals(expectedAllRollEffects, RollEffectCalculator.calculateAllPossibleRollEffects(game));
    }

    @Test
    public void calculateAllPossibleRollEffects_player2() throws GameMechanicException {
        Game game = createTestGame(2);
        Map<Integer, Map<Integer, Integer>> expectedAllRollEffects = new HashMap<>();
        expectedAllRollEffects.put(1,
                Map.of(
                        1,1,
                        2,1,
                        3,1,
                        4,2
                ));
        expectedAllRollEffects.put(2,
                Map.of(
                        1,0,
                        2,3,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(3,
                Map.of(
                        1,1,
                        2,0,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(4,
                Map.of(
                        1,0,
                        2,3,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(5,
                Map.of(
                        1,0,
                        2,0,
                        3,1,
                        4,3
                ));
        expectedAllRollEffects.put(6,
                Map.of(
                1,0,
                2,0,
                3,0,
                4,0
                ));
        expectedAllRollEffects.put(7,
                Map.of(
                        1,0,
                        2,12,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(8, noPlayerEffects);
        expectedAllRollEffects.put(9,
                Map.of(
                        1,1,
                        2,-1,
                        3,10,
                        4,0
                ));
        expectedAllRollEffects.put(10,
                Map.of(
                        1,1,
                        2,5,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(11,
                Map.of(
                        1,0,
                        2,18,
                        3,0,
                        4,0
                ));
        expectedAllRollEffects.put(12,
                Map.of(
                        1,0,
                        2,18,
                        3,0,
                        4,0
                ));
        assertEquals(expectedAllRollEffects, RollEffectCalculator.calculateAllPossibleRollEffects(game));
    }

    @Test
    public void calculateAllPossibleRollEffects_player4_purpleValid() throws GameMechanicException {
        Game game = createTestGame(4);
        Map<Integer, Integer> sixEffects = Map.of(
                1,-2,
                2,-1,
                3,-7,
                4,10
        );
        assertEquals(sixEffects, RollEffectCalculator.calculateAllPossibleRollEffects(game).get(6));
    }

    private Game createTestGame(Integer currentPlayerNumber) throws GameMechanicException {
        Game game = new Game();

        Player player1 = new Player(1);
        player1.setCoins(4);
        player1.getLandmarks().addAll(Set.of(Landmark.SHOPPING_MALL, Landmark.TRAIN_STATION));
        player1.getStock().putAll(Map.of(
                Card.WHEAT, 1,
                Card.BAKERY, 2,
                Card.CAFE, 2,
                Card.CONVENIENCE_STORE, 3,
                Card.FURNITURE_FACTORY, 1,
                Card.FAMILY_RESTAURANT, 1
        ));

        Player player2 = new Player(2);
        player2.setCoins(1);
        player2.getLandmarks().addAll(Set.of(Landmark.TRAIN_STATION, Landmark.AMUSEMENT_PARK));
        player2.getStock().putAll(Map.of(
                Card.WHEAT, 1,
                Card.BAKERY, 1,
                Card.RANCH, 2,
                Card.CONVENIENCE_STORE, 1,
                Card.CHEESE_FACTORY, 2,
                Card.APPLE_ORCHARD, 2,
                Card.FRUIT_AND_VEGETABLE_GARDEN, 3
        ));

        Player player3 = new Player(3);
        player3.setCoins(7);
        player3.getLandmarks().add(Landmark.TRAIN_STATION);
        player3.getStock().putAll(Map.of(
            Card.WHEAT, 1,
            Card.BAKERY, 1,
            Card.CAFE, 1,
            Card.FOREST, 1,
            Card.FURNITURE_FACTORY, 2,
            Card.MINE, 2,
            Card.FAMILY_RESTAURANT, 1
        ));

        Player player4 = new Player(4);
        player4.setCoins(2);
        player4.getStock().putAll(Map.of(
                Card.WHEAT, 2,
                Card.BAKERY, 1,
                Card.FOREST, 3,
                Card.STADIUM, 1,
                Card.TV_STATION, 1
        ));

        game.setPlayers(new HashSet<>(Set.of(player1, player2, player3, player4)));
        game.setCurrentPlayerNumber(currentPlayerNumber);
        return game;
    }

}
