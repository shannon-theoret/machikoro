package com.shannontheoret.machikoro;

import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameMechanicException;
import com.shannontheoret.machikoro.utilities.PlayerStockUtilities;
import com.shannontheoret.machikoro.utilities.RollBenefitAnalyzer;
import com.shannontheoret.machikoro.utilities.RollEffectCalculator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StrategicTurnDecisionEngine {
    private Strategy strategy;
    private Game game;
    private Map<Integer, Map<Integer, Integer>> allRollEffects;
    private final Double RED_COMPETITION_FACTOR = 0.5;
    private final Double RED_FACTOR = 0.7;
    private final Double PERCENTAGE_ROLL_TWO_FUTURE_WITH_NO_TRAIN_STATION = 0.1;
    private final Double GREEN_STRATEGY_BLUE_BENEFIT_FACTOR = 1.5;
    private final Double GREEN_STRATEGY_GREEN_FACTOR = 2.0;
    private final Double GREEN_STRATEGY_RADIO_FACTOR = 3.0;
    private final Double PURPLE_FACTOR = 0.7;
    private final Double BEST_ROLL_OPTIMIST_FACTOR = 0.2;
    private final Double PERCENTAGE_ROLL_TWO_WITH_TRAIN_STATION = 0.5;
    private final Integer MAXIMUM_BENEFITIAL_GREEN_STRATEGY_BLUE_CARDS = 4;

    public StrategicTurnDecisionEngine(Map<StrategyName, Integer> strategyDegrees, Game game) throws GameMechanicException {
        this.strategy = new Strategy(strategyDegrees);
        this.game = game;
        this.allRollEffects = RollEffectCalculator.calculateAllPossibleRollEffects(game);
    }

    public boolean rollSingleDice() {
        if (!game.getCurrentPlayer().hasTrainStation()) {
            return true;
        }
        return worthRollingSingleDice();
    }

    public boolean reroll() throws GameMechanicException {
        Map<Integer, Integer> rollEffects = allRollEffects.get(game.getDieTotal());
        if (isBenefitialDoublesRollWithAmusementPark()) {
            return false;
        }
        Integer currentBenefit = rollEffects.getOrDefault(game.getCurrentPlayerNumber(), 0);
        Integer otherPlayerBenefit = 0;
        for (Map.Entry<Integer, Integer> playerEffect : rollEffects.entrySet()) {
            if (playerEffect.getKey() != game.getCurrentPlayerNumber()) {
                otherPlayerBenefit += playerEffect.getValue();
            }
        }
        Double averageBenefit;
        Integer bestCaseBenefit;
        if (game.didRollTwoDice()) { //player will make the same decision on number of dice rolled upon reroll
            averageBenefit = RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
            bestCaseBenefit = RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        } else {
            averageBenefit = RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
            bestCaseBenefit = RollBenefitAnalyzer.calculateBestCaseScenarioForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        }
        return strategy.reroll(currentBenefit, otherPlayerBenefit, bestCaseBenefit, averageBenefit);
    }

    public Integer choosePlayerToStealFrom() throws GameMechanicException {
        Integer otherPlayerNumberWithMostMoney = game.getNextPlayer(game.getCurrentPlayerNumber()).getNumber();
        Integer mostMoneyAmount = 0;
        Set<Player> playersWithAllCoins = new HashSet<>();
        for (Player player : game.getPlayers()) {
            if (player.getNumber() != game.getCurrentPlayerNumber()) {
                if (player.getCoins() > mostMoneyAmount) {
                    otherPlayerNumberWithMostMoney = player.getNumber();
                }
                if (player.getCoins() > Card.TV_STATION.getAmountGained()) {
                    playersWithAllCoins.add(player);
                }
            }
        }

        if (playersWithAllCoins.size() <= 1) {
            return otherPlayerNumberWithMostMoney;
        } else {
            return playersWithAllCoins.stream()
                    .max(Comparator.comparingDouble(Player::getProgress))
                    .map(Player::getNumber)
                    .orElse(otherPlayerNumberWithMostMoney);
        }
    }

    public BuyingDecision makeBuyingDecision() throws GameMechanicException {
        Set<BuyingDecision> allBuyingDecisions = new HashSet<>();
        for (Card card : Card.values()) {
            if (considerPurchase(card)) {
                allBuyingDecisions.add(evaluateCard(card));
            }
        }
        if (considerPurchase(Landmark.TRAIN_STATION)) {
            allBuyingDecisions.add(evaluateTrainStation());
        }
        if (considerPurchase(Landmark.SHOPPING_MALL)) {
            allBuyingDecisions.add(evaluateShoppingMall());
        }
        if (considerPurchase(Landmark.AMUSEMENT_PARK)) {
            allBuyingDecisions.add(evaluateAmusementPark());
        }
        if (considerPurchase(Landmark.RADIO_TOWER)) {
            allBuyingDecisions.add(evaluateRadioTower());
        }
        if (allBuyingDecisions.size() == 0) {
            return BuyingDecision.endTurn();
        }
        if (allBuyingDecisions.size() == 1) {
            return allBuyingDecisions.iterator().next();
        }
        return strategy.makeBuyingDecisions(allBuyingDecisions, game.getCurrentPlayer().getCoins());
    }

    private BuyingDecision evaluateTrainStation() {
        BuyingDecision buyingDecision = new BuyingDecision(Landmark.TRAIN_STATION);
        Double averageGainOneDie = RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double averageGainTwoDice = RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Integer bestCoinTwoDice = RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainOneDie = RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainTwoDie = RollBenefitAnalyzer.calculateAverageGainForOtherPlayersForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        if (!strategy.rollSingle(averageGainOneDie, averageGainTwoDice, bestCoinTwoDice, otherAverageGainOneDie, otherAverageGainTwoDie)) {
            buyingDecision.setAverageBenefitPerRound(averageGainTwoDice - averageGainOneDie);
            buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, otherAverageGainOneDie - otherAverageGainTwoDie);
            buyingDecision.getStrategicValue().put(StrategyName.OPTIMIST, bestCoinTwoDice * BEST_ROLL_OPTIMIST_FACTOR);
        } else {
            buyingDecision.setAverageBenefitPerRound(0.0);
        }
        return buyingDecision;
    }

    private BuyingDecision evaluateShoppingMall() throws GameMechanicException {
        BuyingDecision buyingDecision = new BuyingDecision(Landmark.SHOPPING_MALL);
        Double totalAverageBenefit = 0.0;
        Double totalAverageAttack = 0.0;
        Player currentPlayer = game.getCurrentPlayer();
        for (Map.Entry<Card, Integer> cardEntry : currentPlayer.getStock().entrySet()) {
            Card card = cardEntry.getKey();
            if (card.getCategory() == CardCategory.STORE || card.getCategory() == CardCategory.CUP) {
                Double benefitFromCard = 0.0;
                for (Integer rollValue : card.getRolls()) {
                     if (currentPlayer.hasTrainStation()) {
                         benefitFromCard += 1 * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue);
                     } else {
                         benefitFromCard += 1 * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY;
                     }
                }
                if (card.isSteals()) {
                    totalAverageAttack += benefitFromCard;
                }
                totalAverageBenefit += benefitFromCard;
            }
        }
        buyingDecision.setAverageBenefitPerRound(totalAverageBenefit);
        buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, totalAverageAttack);
        return buyingDecision;
    }

    private BuyingDecision evaluateAmusementPark() throws GameMechanicException {
        BuyingDecision buyingDecision = new BuyingDecision(Landmark.AMUSEMENT_PARK);
        if (game.getCurrentPlayer().hasTrainStation()) {
            Double averageGainTwoDice = RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
            buyingDecision.setAverageBenefitPerRound(averageGainTwoDice * RollBenefitAnalyzer.DOUBLES_PROBABILITY);
        } else {
            buyingDecision.setAverageBenefitPerRound(0.0);
        }
        return buyingDecision;
    }

    private BuyingDecision evaluateRadioTower() throws GameMechanicException {
        BuyingDecision buyingDecision = new BuyingDecision(Landmark.RADIO_TOWER);
        Integer bestCoins = RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double bestCoinsProbability = RollBenefitAnalyzer.calculateBestCaseScenarioProbabilityForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        buyingDecision.setAverageBenefitPerRound(bestCoins * bestCoinsProbability * 2);
        buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        buyingDecision.getStrategicValue().put(StrategyName.FACTORY_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        return buyingDecision;
    }

    private BuyingDecision evaluateCard(Card card) throws GameMechanicException {
        BuyingDecision buyingDecision = new BuyingDecision(card);
        Player currentPlayer = game.getCurrentPlayer();
        Double currentPlayerOenDie;
        Double currentPlayerTwoDice;
        if (game.getCurrentPlayer().hasTrainStation()) {
            currentPlayerOenDie = 1.0 - PERCENTAGE_ROLL_TWO_WITH_TRAIN_STATION;
            currentPlayerTwoDice = PERCENTAGE_ROLL_TWO_WITH_TRAIN_STATION;
        } else {
            currentPlayerOenDie = 1.0 - PERCENTAGE_ROLL_TWO_FUTURE_WITH_NO_TRAIN_STATION;
            currentPlayerTwoDice = PERCENTAGE_ROLL_TWO_FUTURE_WITH_NO_TRAIN_STATION;
        }
        Double otherPlayerTwoDice = 0.0;
        Double otherPlayerOneDie = 0.0;
        for (Player player : game.getPlayers()) {
            if (player.getNumber() != currentPlayer.getNumber()) {
                if (player.hasTrainStation()) {
                    otherPlayerOneDie += 1.0 - PERCENTAGE_ROLL_TWO_WITH_TRAIN_STATION;
                    otherPlayerTwoDice += PERCENTAGE_ROLL_TWO_WITH_TRAIN_STATION;
                } else {
                    otherPlayerOneDie += 1.0 - PERCENTAGE_ROLL_TWO_FUTURE_WITH_NO_TRAIN_STATION;
                    otherPlayerTwoDice += PERCENTAGE_ROLL_TWO_FUTURE_WITH_NO_TRAIN_STATION;
                }
            }
        }
        if (card.isSteals()) {
            Integer amountGainedPerPlayer = currentPlayer.hasShoppingMall()? card.getAmountGained() + 1 : card.getAmountGained();
            Double totalAverageBenefitIfCoinsAvailable = 0.0;
            for (int rollValue : card.getRolls()) {
                totalAverageBenefitIfCoinsAvailable += amountGainedPerPlayer * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue) * otherPlayerTwoDice;
                if (rollValue <= 6) {
                    totalAverageBenefitIfCoinsAvailable += amountGainedPerPlayer * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY * otherPlayerOneDie;
                }
            }
            Integer competitionForCoins = GameRules.STARTING_STOCK - game.getGameStock().get(card);
            Double competitionFactor = Math.min(1.0, (competitionForCoins + 1) * RED_COMPETITION_FACTOR);
            Double totalAverageBenefit =  totalAverageBenefitIfCoinsAvailable / competitionFactor;
            buyingDecision.setAverageBenefitPerRound(totalAverageBenefit);
            buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, totalAverageBenefit);
        } else if (card.isOnPlayersTurn()) { //green and blue benefits
            Game gameAfterPurchase = game.deepCopy();
            gameAfterPurchase.getCurrentPlayer().addCard(card);
            Double totalAverageBenefit = 0.0;
            for (int rollValue : card.getRolls()) {
                int amountGained = RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(gameAfterPurchase, rollValue) - RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, rollValue);
                if (rollValue <= 6) {
                    totalAverageBenefit += amountGained * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY * currentPlayerOenDie;
                    if (card.isOnAnyonesTurn()) {
                        totalAverageBenefit += amountGained * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY * otherPlayerOneDie;
                    }
                }
                totalAverageBenefit += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue) * currentPlayerTwoDice;
                if (card.isOnAnyonesTurn()) {
                    totalAverageBenefit += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue) * otherPlayerTwoDice;
                }
            }
            Card greenCardAffected = null;
            if (card.getCategory() == CardCategory.GRAIN) {
                if (PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.GRAIN) < MAXIMUM_BENEFITIAL_GREEN_STRATEGY_BLUE_CARDS) {
                    buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
                }
                if (currentPlayer.getStock().containsKey(Card.FRUIT_AND_VEGETABLE_GARDEN)) {
                    greenCardAffected = Card.FRUIT_AND_VEGETABLE_GARDEN;
                }
            }
            if (card.getCategory() == CardCategory.COW) {
                if (PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.COW) < MAXIMUM_BENEFITIAL_GREEN_STRATEGY_BLUE_CARDS) {
                    buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
                }
                if (currentPlayer.getStock().containsKey(Card.CHEESE_FACTORY)) {
                    greenCardAffected = Card.CHEESE_FACTORY;
                }
            }
            if (card.getCategory() == CardCategory.GEAR) {
                if (PlayerStockUtilities.getCountOfCardsInCategory(currentPlayer.getStock(), CardCategory.GEAR) < MAXIMUM_BENEFITIAL_GREEN_STRATEGY_BLUE_CARDS) {
                    buyingDecision.getStrategicValue().put(StrategyName.FACTORY_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
                }
                if (currentPlayer.getStock().containsKey(Card.FURNITURE_FACTORY)) {
                    greenCardAffected = Card.FURNITURE_FACTORY;
                }
            }
            if (greenCardAffected != null) {
                for (int rollValue : greenCardAffected.getRolls()) {
                    int amountGained = RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(gameAfterPurchase, rollValue) - RollEffectCalculator.calculateGreenAndBlueEffectsForCurrentPlayer(game, rollValue);
                    //all green cards in effect are > 6
                    totalAverageBenefit += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue) * currentPlayerTwoDice;
                }
            }
            buyingDecision.setAverageBenefitPerRound(totalAverageBenefit);
            if (card == Card.FRUIT_AND_VEGETABLE_GARDEN) {
                buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
            } else if (card == Card.CHEESE_FACTORY) {
                buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
            } else if (card == Card.FURNITURE_FACTORY) {
                buyingDecision.getStrategicValue().put(StrategyName.FACTORY_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
            }
        }
        if (card.getCategory() == CardCategory.PURPLE) {
            Integer amountGainedIfCoinsAvailable = 0;
            if (card == Card.STADIUM) {
                amountGainedIfCoinsAvailable = card.getAmountGained() * (game.getPlayers().size() - 1);
            } else if (card == Card.TV_STATION) {
                amountGainedIfCoinsAvailable = card.getAmountGained();
            }
            Double averageAmountGainedPerRound = (amountGainedIfCoinsAvailable * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY * currentPlayerOenDie + amountGainedIfCoinsAvailable * RollBenefitAnalyzer.diceRollProbabilityTwoDice(Card.ALL_PURPLE_ROLL) * currentPlayerTwoDice) * PURPLE_FACTOR;
            buyingDecision.setAverageBenefitPerRound(averageAmountGainedPerRound);
            buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, averageAmountGainedPerRound);
        }
        return buyingDecision;
    }

    private boolean worthRollingSingleDice() {
        Double averageGainOneDie = RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double averageGainTwoDice = RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Integer bestCoinTwoDice = RollBenefitAnalyzer.calculateBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainOneDie = RollBenefitAnalyzer.calculateAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainTwoDie = RollBenefitAnalyzer.calculateAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        return strategy.rollSingle(averageGainOneDie,averageGainTwoDice,bestCoinTwoDice,otherAverageGainOneDie, otherAverageGainTwoDie);
    }

    private boolean isBenefitialDoublesRollWithAmusementPark() throws GameMechanicException {
        Map<Integer, Integer> rollBenefits = allRollEffects.get(game.getDieTotal());
        if (game.isDoubles() && game.getCurrentPlayer().hasAmusementPark() && ((game.getCurrentPlayer().getCoins() > 0) || rollBenefits.getOrDefault(game.getCurrentPlayerNumber(), 0) > 0)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean considerPurchase(Card card) throws GameMechanicException {
        if (game.getGameStock().getOrDefault(card ,0) == 0) {
            return false;
        }
        if (card.getCategory() == CardCategory.PURPLE && game.getCurrentPlayer().getStock().containsKey(card)) {
            return false;
        }
        if (strategy.considerSavingForCards(game.getCurrentPlayer().getCoins())) {
            return true;
        } else {
            return game.getCurrentPlayer().getCoins() >= card.getCost();
        }
    }

    private boolean considerPurchase(Landmark landmark) throws GameMechanicException {
        if (game.getCurrentPlayer().hasLandmark(landmark)) {
            return false;
        }
        if (strategy.considerSavingForLandmarks(game.getCurrentPlayer().getCoins())) {
            return true;
        } else {
            return game.getCurrentPlayer().getCoins() >= landmark.getCost();
        }
    }

}


