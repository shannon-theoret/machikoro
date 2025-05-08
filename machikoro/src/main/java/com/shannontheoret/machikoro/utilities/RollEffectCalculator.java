package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.CardCategory;
import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameMechanicException;

import java.util.*;

public class RollEffectCalculator {
    public static Map<Integer, Integer> calculateRedCardEffects(Game game, Integer roll) throws GameMechanicException {
        Map<Integer, Integer> playerRollEffects = new HashMap<>();
        Player playerToSteal = game.getPreviousPlayer(game.getCurrentPlayerNumber());
        Player currentPlayer = game.getCurrentPlayer();
        Integer currentCoins = currentPlayer.getCoins();

        while (playerToSteal.getNumber() != game.getCurrentPlayerNumber()) {
            Set<Card> relevantCards = PlayerStockUtilities.getRedCardsForRoll(playerToSteal.getStock(), roll);
            Integer stealingPlayerGains = 0;

            for (Card card : relevantCards) {
                if (card.isBasic()) {
                    int amountGainedPerCard = card.getAmountGained();
                    if (playerToSteal.hasShoppingMall() && card.getCategory() == CardCategory.CUP) {
                        amountGainedPerCard++;
                    }
                    int amountToSteal = amountGainedPerCard * playerToSteal.getStock().getOrDefault(card, 0);
                    if (currentCoins < amountToSteal) {
                        stealingPlayerGains += currentCoins;
                        currentCoins = 0;
                    } else {
                        stealingPlayerGains += amountToSteal;
                        currentCoins -= amountToSteal;
                    }
                }
            }

            playerRollEffects.put(playerToSteal.getNumber(), stealingPlayerGains);
            playerToSteal = game.getPreviousPlayer(playerToSteal.getNumber());
        }

        int currentPlayerCoinChange = currentCoins - currentPlayer.getCoins();
        playerRollEffects.put(currentPlayer.getNumber(), currentPlayerCoinChange);

        return playerRollEffects;
    }

    public static Integer calculateGreenAndBlueEffectsForCurrentPlayer(Game game, Integer roll) throws GameMechanicException {
        Player currentPlayer = game.getCurrentPlayer();
        Set<Card> relevantCards = PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(currentPlayer.getStock(), roll);
        Integer amountToAdd = 0;
        for (Card card : relevantCards) {
            if (card.isBasic()) {
                int amountGainedPerCard = card.getAmountGained();
                if (currentPlayer.hasShoppingMall() && card.getCategory() == CardCategory.STORE) {
                    amountGainedPerCard++;
                }
                amountToAdd += amountGainedPerCard * currentPlayer.getStock().getOrDefault(card, 0);
            } else if (card == Card.CHEESE_FACTORY) {
                int numberOfCows = PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.COW);
                amountToAdd += numberOfCows * card.getAmountGained() * currentPlayer.getStock().getOrDefault(card, 0);
            } else if (card == Card.FURNITURE_FACTORY) {
                int countOfGearCards = PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.GEAR);
                amountToAdd += countOfGearCards * card.getAmountGained() * currentPlayer.getStock().getOrDefault(card, 0);
            } else if (card == Card.FRUIT_AND_VEGETABLE_GARDEN) {
                int countOfGrainCards = PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.GRAIN); // Use utility method here
                amountToAdd += countOfGrainCards * card.getAmountGained() * currentPlayer.getStock().getOrDefault(card, 0); // Use utility method here
            }
        }
        return amountToAdd;
    }

    public static Map<Integer, Integer> calculateOtherPlayersBlueEffects(Game game, Integer roll) throws GameMechanicException {
        Map<Integer, Integer> playerRollEffects = new HashMap<>();
        Player otherPlayer = game.getNextPlayer(game.getCurrentPlayerNumber());
        while (otherPlayer.getNumber() != game.getCurrentPlayerNumber()) {
            Set<Card> relevantCards = PlayerStockUtilities.getBlueCardsForRoll(otherPlayer.getStock(), roll);
            Integer amountToAdd = 0;
            for (Card card : relevantCards) {
                if (card.isBasic()) {
                    amountToAdd += card.getAmountGained() * otherPlayer.getStock().getOrDefault(card, 0);
                }
            }
            playerRollEffects.put(otherPlayer.getNumber(),amountToAdd);
            otherPlayer = game.getNextPlayer(otherPlayer.getNumber());
        }
        return playerRollEffects;
    }

    public static Map<Integer, Integer> calculateStadiumEffects(Game game, Integer roll) throws GameMechanicException {
        Map<Integer, Integer> playerRollEffects = new HashMap<>();
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getStock().containsKey(Card.STADIUM) && Card.STADIUM.rollApplies(roll)) {
            Integer amountToAddToCurrentPlayer = 0;
            Player playerToStealFrom = game.getNextPlayer(currentPlayer.getNumber());
            while (playerToStealFrom.getNumber() != currentPlayer.getNumber()) {
                int amountToSteal = 0;
                if (playerToStealFrom.getCoins() < Card.STADIUM.getAmountGained()) {
                    amountToSteal = playerToStealFrom.getCoins();
                } else {
                    amountToSteal = Card.STADIUM.getAmountGained();
                }
                amountToAddToCurrentPlayer += amountToSteal;
                playerRollEffects.put(playerToStealFrom.getNumber(), -amountToSteal);
                playerToStealFrom = game.getNextPlayer(playerToStealFrom.getNumber());
            }
            playerRollEffects.put(currentPlayer.getNumber(), amountToAddToCurrentPlayer);
        } else {
            for (int playerNumber=1; playerNumber<= game.getPlayers().size(); playerNumber++) {
                playerRollEffects.put(playerNumber, 0);
            }
        }
        return playerRollEffects;
    }

    public static Map<Integer, Integer> calculateMaximumTVStationEffect(Game game, Integer roll) throws GameMechanicException {
        Map<Integer, Integer> playerRollEffects = new HashMap<>();
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer.getStock().containsKey(Card.TV_STATION) && Card.TV_STATION.rollApplies(roll)) {
            Integer amountToSteal = 0;
            Player otherPlayer = game.getNextPlayer(currentPlayer.getNumber());
            Integer bestPlayerNumberToStealFrom = otherPlayer.getNumber();
            while (otherPlayer.getNumber() != currentPlayer.getNumber()) {
                if (otherPlayer.getCoins() > amountToSteal) {
                    amountToSteal = Math.min(otherPlayer.getCoins(), Card.TV_STATION.getAmountGained());
                    bestPlayerNumberToStealFrom = otherPlayer.getNumber();
                }
                otherPlayer = game.getNextPlayer(otherPlayer.getNumber());
            }
            playerRollEffects.put(bestPlayerNumberToStealFrom, -amountToSteal);
            playerRollEffects.put(currentPlayer.getNumber(), amountToSteal);
        }
        for (int playerNumber=1; playerNumber<= game.getPlayers().size(); playerNumber++) {
            if (!playerRollEffects.containsKey(playerNumber)) {
                playerRollEffects.put(playerNumber, 0);
            }
        }

        return playerRollEffects;
    }

    //roll: player number, coins gained
    public static Map<Integer, Map<Integer, Integer>> calculateAllPossibleRollEffects(Game game) throws GameMechanicException {
        Map<Integer, Map<Integer, Integer>> allPossibleRollEffects = new HashMap<>();
        for (int roll=1; roll <=12; roll++) {
            Map<Integer, Integer> playerRollEffects = new HashMap<>();
            mergePlayerRollEffects(playerRollEffects, calculateRedCardEffects(game, roll));
            playerRollEffects.merge(game.getCurrentPlayerNumber(), calculateGreenAndBlueEffectsForCurrentPlayer(game, roll), Integer::sum);
            mergePlayerRollEffects(playerRollEffects, calculateOtherPlayersBlueEffects(game, roll));
            mergePlayerRollEffects(playerRollEffects, calculateStadiumEffects(game, roll));
            mergePlayerRollEffects(playerRollEffects, calculateMaximumTVStationEffect(game, roll));
            allPossibleRollEffects.put(roll, playerRollEffects); //roll value always present in map even if map of effects is empty
        }
        return allPossibleRollEffects;
    }

    private static void mergePlayerRollEffects(Map<Integer, Integer> target, Map<Integer, Integer> source) {
        for (Map.Entry<Integer, Integer> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }
}
