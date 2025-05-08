package com.shannontheoret.machikoro.utilities;

import java.util.Map;

public class RollBenefitAnalyzer {

    private static final Map<Integer, Double> TWO_D6_PROBABILITIES = Map.ofEntries(
            Map.entry(2, 1.0 / 36),
            Map.entry(3, 2.0 / 36),
            Map.entry(4, 3.0 / 36),
            Map.entry(5, 4.0 / 36),
            Map.entry(6, 5.0 / 36),
            Map.entry(7, 6.0 / 36),
            Map.entry(8, 5.0 / 36),
            Map.entry(9, 4.0 / 36),
            Map.entry(10, 3.0 / 36),
            Map.entry(11, 2.0 / 36),
            Map.entry(12, 1.0 / 36)
    );

    public static final Double SINGLE_ROLL_PROBABILITY = 1.0/6.0;

    public static final Double DOUBLES_PROBABILITY = 1.0/6.0;

    public static Double diceRollProbabilityTwoDice(Integer rollValue) {
            if (TWO_D6_PROBABILITIES.containsKey(rollValue)) {
                return TWO_D6_PROBABILITIES.get(rollValue);
            } else {
                return 0.0;
            }
    }

    public static Double calculateAverageGainForOneDieRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer playerNumber) {
        Double averageCoinGain = 0.0;
        for (int rollValue=1; rollValue<=6; rollValue++) {
            averageCoinGain += (double) allRollEffects.get(rollValue).getOrDefault(playerNumber, 0) * SINGLE_ROLL_PROBABILITY;
        }
        return averageCoinGain;
    }

    public static Double calculateAverageGainForTwoDiceRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer playerNumber) {
        Double averageCoinGain = 0.0;
        for (int rollValue=2; rollValue<=12; rollValue++) {
            averageCoinGain += (double) allRollEffects.get(rollValue).getOrDefault(playerNumber, 0) * diceRollProbabilityTwoDice(rollValue);
        }
        return averageCoinGain;
    }

    public static Double calculateAverageGainForOtherPlayersForOneDieRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer currentPlayerNumber) {
        Double averageCoinGain = 0.0;
        for (int rollValue=1; rollValue<=6; rollValue++) {
            for(Map.Entry<Integer, Integer> rollEffect : allRollEffects.get(rollValue).entrySet()) {
                if (rollEffect.getKey() != currentPlayerNumber) {
                    averageCoinGain += (double) rollEffect.getValue() * SINGLE_ROLL_PROBABILITY;
                }
            }
        }
        return averageCoinGain;
    }

    public static Double calculateAverageGainForOtherPlayersForTwoDiceRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer currentPlayerNumber) {
        Double averageCoinGain = 0.0;
        for (int rollValue=2; rollValue<=12; rollValue++) {
            for(Map.Entry<Integer, Integer> rollEffect : allRollEffects.get(rollValue).entrySet()) {
                if (rollEffect.getKey() != currentPlayerNumber) {
                    averageCoinGain += (double) rollEffect.getValue() * diceRollProbabilityTwoDice(rollValue);
                }
            }
        }
        return averageCoinGain;
    }

    public static Integer calculateBestCaseScenarioForOneDieRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer playerNumber) {
        Integer maxCoins = 0;
        for (int rollValue = 1; rollValue <= 6; rollValue++) {
            Integer coinsForRoll = allRollEffects.get(rollValue).getOrDefault(playerNumber, 0);
            maxCoins = Math.max(maxCoins, coinsForRoll);
        }
        return maxCoins;
    }

    public static Integer calculateBestCaseScenarioForTwoDiceRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer playerNumber) {
        Integer maxCoins = 0;
        for (int rollValue = 2; rollValue <= 12; rollValue++) {
            Integer coinsForRoll = allRollEffects.get(rollValue).getOrDefault(playerNumber, 0);
            maxCoins = Math.max(maxCoins, coinsForRoll);
        }
        return maxCoins;
    }

    public static Double calculateBestCaseScenarioProbabilityForTwoDiceRolled(Map<Integer, Map<Integer, Integer>> allRollEffects, Integer playerNumber) {
        Integer maxCoins = 0;
        Double bestCaseProbability = 1.0; //If the best case is that the player gets no coins there is a 100% chance this will happen barring some strange red card scenarios
        for (int rollValue = 2; rollValue <= 12; rollValue++) {
            Integer coinsForRoll = allRollEffects.get(rollValue).getOrDefault(playerNumber, 0);
            if (coinsForRoll > maxCoins || (coinsForRoll == maxCoins && bestCaseProbability < diceRollProbabilityTwoDice(rollValue))) {
                maxCoins = coinsForRoll;
                bestCaseProbability = diceRollProbabilityTwoDice(rollValue);
            }
        }
        return bestCaseProbability;
    }


}
