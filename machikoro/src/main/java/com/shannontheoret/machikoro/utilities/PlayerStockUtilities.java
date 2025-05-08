package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.CardCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerStockUtilities {


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

    public static Set<Card> getGreenAndBlueCardsForPlayerRoll(Map<Card, Integer> stock, Integer roll) {
        Set<Card> relevantCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnPlayersTurn() && card.getCategory() != CardCategory.PURPLE)
                .collect(Collectors.toSet());
        return relevantCards;
    }

}
