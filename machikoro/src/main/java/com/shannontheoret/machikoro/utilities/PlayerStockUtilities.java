package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.CardCategory;
import com.shannontheoret.machikoro.exception.GameMechanicException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerStockUtilities {

    public static final Map<Integer, Double> TWO_D6_PROBABILITIES = Map.ofEntries(
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

    public static Double diceRollProbability(Integer rollValue, boolean rollTwo) {
        if (!rollTwo) {
            if (rollValue >= 1 && rollValue <= 6) {
                return (double) 1/6;
            } else {
                return 0.0;
            }
        } else {
            if (TWO_D6_PROBABILITIES.containsKey(rollValue)) {
                return TWO_D6_PROBABILITIES.get(rollValue);
            } else {
                return 0.0;
            }
        }
    }


    public static Integer getCountOfCardsInCategory(Map<Card, Integer> stock, CardCategory category) {
        int countOfCategoryCards = 0;
        List<Card> categoryCards = stock.keySet()
                .stream()
                .filter(card -> card.getCategory() == category)
                .collect(Collectors.toList());
        for (Card cardCategory : categoryCards) {
            countOfCategoryCards += stock.get(cardCategory);
        }
        return countOfCategoryCards;
    }

    public static Set<Card> getRedCardsForRoll(Map<Card, Integer> stock, Integer roll) {
        Set<Card> relevantCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isSteals())
                .collect(Collectors.toSet());
        return relevantCards;
    }

    public static Set<Card> getBlueCardsForRoll(Map<Card, Integer> stock, Integer roll) {
        Set<Card> relevantCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnAnyonesTurn())
                .collect(Collectors.toSet());
        return relevantCards;
    }

    public static Set<Card> getCardsForPlayerRoll(Map<Card, Integer> stock, Integer roll) {
        Set<Card> relevantCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnPlayersTurn() && card.getCategory() != CardCategory.PURPLE)
                .collect(Collectors.toSet());
        return relevantCards;
    }

    public static Integer getNumberOfCardsOverSix(Map<Card, Integer> stock) {
        Integer count = 0;
        for (Map.Entry<Card, Integer> entry : stock.entrySet()) {
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
