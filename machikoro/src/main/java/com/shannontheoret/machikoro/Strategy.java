package com.shannontheoret.machikoro;

import java.util.*;


public class Strategy {
    private Map<StrategyName, Integer> strategyDegrees; //each degree is 0 to 3, or booleans are 0 or 1
    private final static double BASIC_DECISION_ROLL_SINGLE_FACTOR = 20.0;
    private final static double OTHER_PLAYER_BENEFIT_REROLL_FACTOR = 5.0;
    private final static double BEST_ROLL_BENEFIT_REROLL_FACTOR = 10.0;
    private final static int MIN_SAVER_SETTING_TO_CONSIDER_SAVING_FOR_CARDS = 3;
    private final static int MIN_SAVER_SETTING_TO_CONSIDER_SAVING_FOR_LANDMARKS = 1;
    private final static int MAX_SAVER_SETTING_TO_MAKE_CHEAP_PURCHASE = 2;
    private final static double SAVING_VALUE_THRESHOLD = 3.0;
    private final int MAX_COST_FOR_CHEAP_PURCHASE = 2;
    private final static int ENOUGH_COINS_TO_CONSIDER_SAVINGS = 3;
    private final static double STRATEGY_WEIGHT = 0.5;
    private final static double PROGRESS_WEIGHT = 5.0;
    private final static double COST_WEIGHT = 0.5;

    public Strategy(Map<StrategyName, Integer> strategyDegrees) {
        this.strategyDegrees = strategyDegrees;
    }

    public Map<StrategyName, Integer> getStrategyDegrees() {
        return strategyDegrees;
    }

    public void setStrategyDegrees(Map<StrategyName, Integer> strategyDegrees) {
        this.strategyDegrees = strategyDegrees;
    }

    public boolean rollSingle(Double averageGainOneDie, Double averageGainTwoDice, Integer bestCoinsTwoDice, Double otherAverageGainOneDie, Double otherAverageGainTwoDice) {
        //positive is one die, negative two dice
        //TODO: tweak weight formulas
        Double basicDecisionWeight = (averageGainOneDie - averageGainTwoDice) * BASIC_DECISION_ROLL_SINGLE_FACTOR;
        Double optimismStrategyWeight = (averageGainOneDie - bestCoinsTwoDice) * strategyDegrees.get(StrategyName.OPTIMIST);
        Double attackStrategyWeight = (otherAverageGainTwoDice - otherAverageGainOneDie) * strategyDegrees.get(StrategyName.ATTACK_FOCUSED);
        return ((basicDecisionWeight + optimismStrategyWeight + attackStrategyWeight) > 0 );
    }

    public boolean reroll(Integer currentBenefit, Integer otherPlayerBenefit, Integer bestCoins, Double averageBenefit) {
        Integer optimism = strategyDegrees.get(StrategyName.OPTIMIST);
        Integer attack = strategyDegrees.get(StrategyName.ATTACK_FOCUSED);
        if (currentBenefit <= 0) {
            return true;
        }
        if ((averageBenefit > currentBenefit) && optimism > 0) {
            return true;
        }
        if (otherPlayerBenefit > (currentBenefit + OTHER_PLAYER_BENEFIT_REROLL_FACTOR/strategyDegrees.get(StrategyName.ATTACK_FOCUSED))) {
            return  true;
        }
        if ((bestCoins - currentBenefit) > BEST_ROLL_BENEFIT_REROLL_FACTOR/optimism) {
            return true;
        }
        return false;
    }

    public BuyingDecision makeBuyingDecisions(Set<BuyingDecision> buyingDecisions, Integer coinsAvailable) {
        for (BuyingDecision buyingDecision : buyingDecisions) {
            buyingDecision.setCompositeScore(calculateCompositeScore(buyingDecision));
        }
        List<BuyingDecision> prioritizedBuyingDecisions = new ArrayList<>(buyingDecisions);
        prioritizedBuyingDecisions.sort(new BuyingDecisionComparator());
        boolean saveUp = false;
        BuyingDecision noPuchase = BuyingDecision.endTurn();
        BuyingDecision bestAffordablePurchase = noPuchase;
        BuyingDecision bestCheapPurchase = noPuchase;
        BuyingDecision bestPurchaseWorthSavingFor = noPuchase;
        for (BuyingDecision buyingDecision : buyingDecisions) {
            if (isPurchaseCheap(buyingDecision, coinsAvailable) && buyingDecision.getCompositeScore() > bestCheapPurchase.getCompositeScore()) {
                bestCheapPurchase = buyingDecision;
            }
            if ((buyingDecision.getCost() <= coinsAvailable) && bestAffordablePurchase.getCompositeScore() < buyingDecision.getCompositeScore()) {
                bestAffordablePurchase = buyingDecision;
            }
        }
        for (BuyingDecision buyingDecision : buyingDecisions) {
            if (isPurchaseWorthSavingFor(buyingDecision, bestAffordablePurchase, coinsAvailable)) {
                return bestCheapPurchase;
            }
        }
        return bestAffordablePurchase;
    }

    public boolean considerSavingForCards(Integer coins) {
        return isCoinsEnoughToConsiderSaving(coins) && isSaverForCard();
    }

    public boolean considerSavingForLandmarks(Integer coins) {
        return isCoinsEnoughToConsiderSaving(coins) && isSaverForLandmark();
    }

    private boolean isCoinsEnoughToConsiderSaving(Integer coins) {
        return coins > ENOUGH_COINS_TO_CONSIDER_SAVINGS;
    }

    private boolean isSaverForCard() {
        return strategyDegrees.getOrDefault(StrategyName.SAVER, 0) >= MIN_SAVER_SETTING_TO_CONSIDER_SAVING_FOR_CARDS;
    }

    private boolean isSaverForLandmark() {
        return strategyDegrees.getOrDefault(StrategyName.SAVER, 0) >= MIN_SAVER_SETTING_TO_CONSIDER_SAVING_FOR_LANDMARKS;
    }

    private double calculateCompositeScore(BuyingDecision decision) {
        double strategyScore = decision.getStrategicValue().entrySet().stream()
                .mapToDouble(entry -> {
                    int degree = strategyDegrees.getOrDefault(entry.getKey(), 0);
                    return entry.getValue() * degree;
                }).sum();

        double benefitScore = decision.getAverageBenefitPerRound();

        double progressScore = decision.getProgressValue();


        return (strategyScore * STRATEGY_WEIGHT
                + benefitScore
                + progressScore * PROGRESS_WEIGHT)/(decision.getCost() * COST_WEIGHT);
    }

    private boolean isPurchaseCheap(BuyingDecision buyingDecision, Integer coins) {
        if (buyingDecision.getCost() > coins) {
            return false;
        } else if (strategyDegrees.getOrDefault(StrategyName.SAVER, 0) > MAX_SAVER_SETTING_TO_MAKE_CHEAP_PURCHASE) {
            return false;
        } else if (buyingDecision.getCost() > MAX_COST_FOR_CHEAP_PURCHASE) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isPurchaseWorthSavingFor(BuyingDecision buyingDecision, BuyingDecision bestAlternative, Integer coinsAvailable) {
        Double differenceInCompositeScore = buyingDecision.getCompositeScore() - bestAlternative.getCompositeScore();
        if (differenceInCompositeScore <= 0) {
            return false;
        }
        Integer coinsToSave = buyingDecision.getCost() - coinsAvailable;
        Double valuePerCoinsToSave = differenceInCompositeScore/coinsToSave;
        return (valuePerCoinsToSave * strategyDegrees.getOrDefault(StrategyName.SAVER,0) > SAVING_VALUE_THRESHOLD);
    }
}
