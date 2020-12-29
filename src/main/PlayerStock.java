package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerStock {

    private Map<Card, Integer> stock = new HashMap<>();

    public int getCardCount(Card card) {
        int count = 0;
        if (stock.containsKey(card)) {
            count = stock.get(card);
        }
        return count;
    }

    public void addCard(Card card, Integer count) {
        stock.put(card, count);
    }

    public void addCard(Card card) {
        if(stock.containsKey(card)) {
            Integer currentCount = stock.get(card);
            currentCount++;
            stock.put(card, currentCount);
        } else {
            stock.put(card, 1);
        }
    }

    public Integer getCountOfCardsInCategory(CardCategory category) {
        int countOfCategoryCards = 0;
        List<Card> categoryCards = stock.keySet()
                .stream()
                .filter(card -> card.getCategory() == category)
                .collect(Collectors.toList());
        for ( Card cardCategory: categoryCards) {
            countOfCategoryCards += stock.get(cardCategory);
        }
        return countOfCategoryCards;
    }

    public Set<Card> getRedCardsForRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isSteals())
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Set<Card> getBlueCardsForRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnAnyonesTurn())
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Set<Card> getCardsForPlayerRoll(Integer roll) {
        Set<Card> releventCards = stock
                .keySet()
                .stream()
                .filter(card -> card.rollApplies(roll) && card.isOnPlayersTurn() & card.getCategory() != CardCategory.PURPLE)
                .collect(Collectors.toSet());
        return releventCards;
    }

    public Map<Integer, Integer> getStockMap() {
        Map<Integer, Integer> stockMap = new HashMap<>();
        for (Entry<Card, Integer> entry : stock.entrySet()) {
            stockMap.put(entry.getKey().ordinal(), entry.getValue());
        }
        return stockMap;
    }







}
