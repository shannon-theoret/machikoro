package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.exception.GameMechanicException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RollBenefitAnalyzerTests {

    private static final Map<Integer, Integer> NO_PLAYER_EFFECTS = Map.of(
            1,0,
            2,0,
            3,0,
            4,0);

    @Test
    public void diceRollProbabilityTwoDice_returnsOneThirtySixthForRoll2_twoDice() {
        assertEquals(1.0 / 36.0, RollBenefitAnalyzer.diceRollProbabilityTwoDice(2));
    }

    @Test
    public void diceRollProbabilityTwoDice_returnsZeroForRoll1() {
        assertEquals(0.0, RollBenefitAnalyzer.diceRollProbabilityTwoDice(1));
    }

    @Test
    public void diceRollProbability_returnsSixThirtySixthsForRoll7() {
        assertEquals(6.0 / 36.0, RollBenefitAnalyzer.diceRollProbabilityTwoDice(7));
    }

    @Test
    public void calculateAverageGainForOneDieRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(25.0/6.0, RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allPossibleRollEffects, 1),1e-6);
    }

    @Test
    public void calculateAverageGainForOneDieRolled_exampleA_otherPlayer() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(1.0/6.0, RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allPossibleRollEffects, 3),1e-6);
    }

    @Test
    public void calculateAverageGainForOneDieRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(16.0/6.0, RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allPossibleRollEffects, 4), 1e-6);
    }

    @Test
    public void calculateAverageGainForTwoDiceRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(57.0/36.0, RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allPossibleRollEffects, 1), 1e-6);
    }

    @Test
    public void calculateAverageGainForTwoDiceRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(148.0/36.0, RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allPossibleRollEffects, 4), 1e-6);
    }

    @Test
    public void calculateAverageGainForOtherPlayersForOneDieRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(1.0, RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForOneDieRolled(allPossibleRollEffects, 1), 1e-6);
    }

    @Test
    public void calculateAverageGainForOtherPlayersForOneDieRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(8.0/6.0, RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForOneDieRolled(allPossibleRollEffects, 4), 1e-6);
    }

    @Test
    public void calculateAverageGainForOtherPlayersForTwoDiceRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(67.0/36.0, RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForTwoDiceRolled(allPossibleRollEffects, 1), 1e-6);
    }

    @Test
    public void calculateAverageGainForOtherPlayersForTwoDiceRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(64.0/36.0, RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForTwoDiceRolled(allPossibleRollEffects, 4), 1e-6);
    }

    @Test
    public void calculateBestCaseScenarioForOneDieRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(12, RollBenefitAnalyzer.calculateBestCaseScenarioForOneDieRolled(allPossibleRollEffects, 1));
    }

    @Test
    public void calculateBestCaseScenarioForOneDieRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(6, RollBenefitAnalyzer.calculateBestCaseScenarioForOneDieRolled(allPossibleRollEffects, 4));
    }

    @Test
    public void calculateBestCaseScenarioForTwoDiceRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(12, RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allPossibleRollEffects, 1));
    }

    @Test
    public void calculateBestCaseScenarioForTwoDiceRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(21, RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allPossibleRollEffects, 4));
    }

    @Test
    public void calculateBestCaseScenarioProbabilityForTwoDiceRolled_exampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleA();
        assertEquals(3.0/36.0, RollBenefitAnalyzer.calculateBestCaseScenarioProbabilityForTwoDiceRolled(allPossibleRollEffects, 1));
    }

    @Test
    public void calculateBestCaseScenarioProbabilityForTwoDiceRolled_exampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = createAllPossibleRollEffectsExampleB();
        assertEquals(2.0/36.0, RollBenefitAnalyzer.calculateBestCaseScenarioProbabilityForTwoDiceRolled(allPossibleRollEffects, 4));
    }

    @Test
    public void calculateBestCaseScenarioProbabilityForTwoDiceRolled_higherProbabilityRollWithSameCoinsLater() throws GameMechanicException {
        Map<Integer, Map<Integer,Integer>> allPossibleRollEffects = new HashMap<>();
        allPossibleRollEffects.put(1, NO_PLAYER_EFFECTS);
        allPossibleRollEffects.put(2, NO_PLAYER_EFFECTS);
        allPossibleRollEffects.put(3, NO_PLAYER_EFFECTS);
        allPossibleRollEffects.put(4, Map.of(
                1,0,
                2, 12,
                3,0,
                4,0
        ));
        allPossibleRollEffects.put(5, Map.of(
                1, 20,
                2,0,
                3,0,
                4,0
        ));
        allPossibleRollEffects.put(6, NO_PLAYER_EFFECTS);
        allPossibleRollEffects.put(7, Map.of(
                1,0,
                2,12,
                3,0,
                4,0
        ));
        allPossibleRollEffects.putAll(Map.of(
                8, NO_PLAYER_EFFECTS,
                9, NO_PLAYER_EFFECTS,
                10, NO_PLAYER_EFFECTS,
                11, NO_PLAYER_EFFECTS,
                12, NO_PLAYER_EFFECTS
        ));
        assertEquals(6.0/36.0, RollBenefitAnalyzer.calculateBestCaseScenarioProbabilityForTwoDiceRolled(allPossibleRollEffects, 2));
    }

    Map<Integer, Map<Integer, Integer>> createAllPossibleRollEffectsExampleA() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRolleEffects = new HashMap<>();

        //player 1 is the current player
        allPossibleRolleEffects.put(1,
                Map.of(
                        1,1,
                        2,1,
                        3,1,
                        4,2
                ));
        allPossibleRolleEffects.put(2,
                Map.of(
                        1,4,
                        2,2,
                        3,0,
                        4,0
                ));
        allPossibleRolleEffects.put(3,
                Map.of(
                        1,3,
                        2,0,
                        3,1,
                        4,0
                ));
        allPossibleRolleEffects.put(4,
                Map.of(
                        1,12,
                        2,0,
                        3,0,
                        4,0
                ));
        allPossibleRolleEffects.put(5,
                Map.of(
                        1,0,
                        2,0,
                        3,1,
                        4,3
                ));
        allPossibleRolleEffects.put(6,
                Map.of(
                        1,5,
                        2,-1,
                        3,-2,
                        4,-2
                ));
        allPossibleRolleEffects.put(7, NO_PLAYER_EFFECTS);
        allPossibleRolleEffects.put(8, NO_PLAYER_EFFECTS);
        allPossibleRolleEffects.put(9,
                Map.of(
                        1,-2,
                        2,0,
                        3,12,
                        4,0));
        allPossibleRolleEffects.put(10,
                Map.of(
                        1,-2,
                        2,6,
                        3,2,
                        4,0
                ));
        allPossibleRolleEffects.put(11, NO_PLAYER_EFFECTS);
        allPossibleRolleEffects.put(12, NO_PLAYER_EFFECTS);
        return allPossibleRolleEffects;
    }

    Map<Integer, Map<Integer, Integer>> createAllPossibleRollEffectsExampleB() throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRolleEffects = new HashMap<>();
        //player 4 is the current player
        allPossibleRolleEffects.put(1,
                Map.of(
                        1,3,
                        2,1,
                        3,1,
                        4,2
                ));
        allPossibleRolleEffects.put(2,
                Map.of(
                        1,2,
                        2,2,
                        3,0,
                        4,3
                ));
        allPossibleRolleEffects.put(3,
                Map.of(
                        1,1,
                        2,0,
                        3,1,
                        4,-2
                ));
        allPossibleRolleEffects.put(4,
                Map.of(
                        1,0,
                        2,0,
                        3,0,
                        4,6
                ));
        allPossibleRolleEffects.put(5,
                Map.of(
                        1,0,
                        2,0,
                        3,1,
                        4,3
                ));
        allPossibleRolleEffects.put(6,
                Map.of(
                        1,0,
                        2,-4,
                        3,0,
                        4,4
                ));
        allPossibleRolleEffects.put(7, Map.of(
                1,0,
                2,0,
                3,0,
                4,6
        ));
        allPossibleRolleEffects.put(8, NO_PLAYER_EFFECTS);
        allPossibleRolleEffects.put(9,
                Map.of(
                        1,0,
                        2,0,
                        3,12,
                        4,0));
        allPossibleRolleEffects.put(10,
                Map.of(
                        1,0,
                        2,6,
                        3,2,
                        4,0
                ));
        allPossibleRolleEffects.put(11,
                Map.of(
                        1,0,
                        2,0,
                        3,0,
                        4,21
                ));
        allPossibleRolleEffects.put(12, Map.of(
                1,0,
                2,0,
                3,0,
                4,21
        ));
        return allPossibleRolleEffects;
    }
}
