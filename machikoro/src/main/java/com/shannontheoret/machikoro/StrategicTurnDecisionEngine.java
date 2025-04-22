package com.shannontheoret.machikoro;

import com.shannontheoret.machikoro.entity.Game;
import com.shannontheoret.machikoro.entity.Player;
import com.shannontheoret.machikoro.exception.GameMechanicException;
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
    private final Double RED_COMPETITION_FACTOR = 0.3;
    private final Double LONG_TERM_FACTOR = 0.5;
    private final Double GREEN_STRATEGY_BLUE_BENEFIT_FACTOR = 1.0;
    private final Double GREEN_STRATEGY_GREEN_FACTOR = 2.0;
    private final Double GREEN_STRATEGY_RADIO_FACTOR = 3.0;
    private final Double PURPLE_FACTOR = 0.7;
    private final Double BEST_ROLL_OPTIMIST_FACTOR = 0.2;

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
            averageBenefit = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
            bestCaseBenefit = RollBenefitAnalyzer.getBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        } else {
            averageBenefit = RollBenefitAnalyzer.getAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
            bestCaseBenefit = RollBenefitAnalyzer.getBestCaseScenarioForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
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
                allBuyingDecisions.add(evalauteCard(card));
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
        Double averageGainOneDie = RollBenefitAnalyzer.getAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double averageGainTwoDice = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Integer bestCoinTwoDice = RollBenefitAnalyzer.getBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainOneDie = RollBenefitAnalyzer.getAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainTwoDie = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        if (strategy.rollSingle(averageGainOneDie, averageGainTwoDice, bestCoinTwoDice, otherAverageGainOneDie, otherAverageGainTwoDie)) {
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
            Double averageGainTwoDice = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
            buyingDecision.setAverageBenefitPerRound(averageGainTwoDice * RollBenefitAnalyzer.DOUBLES_PROBABILITY);
        } else {
            buyingDecision.setAverageBenefitPerRound(0.0);
        }
        return buyingDecision;
    }

    private BuyingDecision evaluateRadioTower() throws GameMechanicException {
        BuyingDecision buyingDecision = new BuyingDecision(Landmark.RADIO_TOWER);
        Integer bestCoins = RollBenefitAnalyzer.getBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double bestCoinsProbability = RollBenefitAnalyzer.getBestCaseScenarioProbabilityForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        buyingDecision.setAverageBenefitPerRound(bestCoins * bestCoinsProbability * 2);
        buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        buyingDecision.getStrategicValue().put(StrategyName.FACTORY_FOCUSED, GREEN_STRATEGY_RADIO_FACTOR);
        return buyingDecision;
    }

    private BuyingDecision evalauteCard(Card card) throws GameMechanicException {
    BuyingDecision buyingDecision = new BuyingDecision(card);
    Player currentPlayer = game.getCurrentPlayer();
    if (card.isSteals()) {
        Double totalAverageBenefitIfCoinsAvailable = 0.0;
        for (Player player : game.getPlayers()) {
            if (player.getNumber() != currentPlayer.getNumber()) {
                Double averageBenefitIfCoinsAvailable = 0.0;
                Integer amountGained = currentPlayer.hasShoppingMall()? card.getAmountGained() + 1 : card.getAmountGained();
                //assumption that if a player has a train station they will roll two dice is limited of course
                if (player.hasTrainStation()) {
                    for (int rollValue : card.getRolls()) {
                        averageBenefitIfCoinsAvailable += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue);
                    }
                } else {
                    for (int rollValue : card.getRolls()) {
                        if (rollValue <= 6) {
                            averageBenefitIfCoinsAvailable += amountGained * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY;
                        } else {
                            //still some benefit long term as they will probably buy train station in future
                            averageBenefitIfCoinsAvailable += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue) * LONG_TERM_FACTOR;
                        }
                    }
                }
                totalAverageBenefitIfCoinsAvailable += averageBenefitIfCoinsAvailable;
            }
        }
        Integer competitionForCoins = GameRules.STARTING_STOCK - game.getGameStock().get(card);
        Double totalAverageBenefit =  totalAverageBenefitIfCoinsAvailable / ((competitionForCoins + 1) * RED_COMPETITION_FACTOR);
        buyingDecision.setAverageBenefitPerRound(totalAverageBenefit);
        buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, totalAverageBenefit);
    } else if (card.isOnPlayersTurn()) { //green and blue benefits
        Game gameAfterPurchase = game.deepCopy();
        gameAfterPurchase.getCurrentPlayer().addCard(card);
        Double totalAverageBenefit = 0.0;
        for (int rollValue : card.getRolls()) {
            int amountGained = RollEffectCalculator.calculateGreenAndBlueEffects(gameAfterPurchase, rollValue) - RollEffectCalculator.calculateGreenAndBlueEffects(game, rollValue);
            if (currentPlayer.hasTrainStation() || rollValue > 6) {
                totalAverageBenefit += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue);
            } else {
                totalAverageBenefit += amountGained * RollBenefitAnalyzer.SINGLE_ROLL_PROBABILITY;
            }
            if (card.isOnAnyonesTurn()) {
                totalAverageBenefit = totalAverageBenefit * game.getPlayers().size();
            }
        }
        Card greenCardAffected = null;
        if (card.getCategory() == CardCategory.GRAIN && currentPlayer.getStock().containsKey(Card.FRUIT_AND_VEGETABLE_GARDEN)) {
            greenCardAffected = Card.FRUIT_AND_VEGETABLE_GARDEN;
            buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
        }
        if (card.getCategory() == CardCategory.COW && currentPlayer.getStock().containsKey(Card.CHEESE_FACTORY)) {
            greenCardAffected = Card.CHEESE_FACTORY;
            buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
        }
        if (card.getCategory() == CardCategory.GEAR && currentPlayer.getStock().containsKey(Card.FURNITURE_FACTORY)) {
            greenCardAffected = Card.FURNITURE_FACTORY;
            buyingDecision.getStrategicValue().put(StrategyName.FACTORY_FOCUSED, GREEN_STRATEGY_BLUE_BENEFIT_FACTOR);
        }
        if (greenCardAffected != null) {
            for (int rollValue : greenCardAffected.getRolls()) {
                int amountGained = RollEffectCalculator.calculateGreenAndBlueEffects(gameAfterPurchase, rollValue) - RollEffectCalculator.calculateGreenAndBlueEffects(game, rollValue);
                totalAverageBenefit += amountGained * RollBenefitAnalyzer.diceRollProbabilityTwoDice(rollValue);
            }
        }
        buyingDecision.setAverageBenefitPerRound(totalAverageBenefit);
        if (card == Card.FRUIT_AND_VEGETABLE_GARDEN) {
            buyingDecision.getStrategicValue().put(StrategyName.FRUIT_AND_VEG_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
        } else if (card == Card.CHEESE_FACTORY) {
            buyingDecision.getStrategicValue().put(StrategyName.CHEESE_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
        } else if (card == Card.FURNITURE_FACTORY) {
            buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, GREEN_STRATEGY_GREEN_FACTOR);
        }
    }
    if (card.getCategory() == CardCategory.PURPLE) {
        Integer amountGainedIfCoinsAvailable = 0;
        if (card == Card.STADIUM) {
            amountGainedIfCoinsAvailable = card.getAmountGained() * (game.getPlayers().size() - 1);
        } else if (card == Card.TV_STATION) {
            amountGainedIfCoinsAvailable = card.getAmountGained();
        }
        Double averageAmountGainedPerRound = amountGainedIfCoinsAvailable * RollBenefitAnalyzer.diceRollProbabilityTwoDice(Card.ALL_PURPLE_ROLL) * PURPLE_FACTOR;
        buyingDecision.setAverageBenefitPerRound(averageAmountGainedPerRound);
        buyingDecision.getStrategicValue().put(StrategyName.ATTACK_FOCUSED, averageAmountGainedPerRound);
    }
    return buyingDecision;
    }

    private boolean worthRollingSingleDice() {
        Double averageGainOneDie = RollBenefitAnalyzer.getAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double averageGainTwoDice = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Integer bestCoinTwoDice = RollBenefitAnalyzer.getBestCaseScenarioForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainOneDie = RollBenefitAnalyzer.getAverageGainForOneDieRolled(allRollEffects, game.getCurrentPlayerNumber());
        Double otherAverageGainTwoDie = RollBenefitAnalyzer.getAverageGainForTwoDiceRolled(allRollEffects, game.getCurrentPlayerNumber());
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


