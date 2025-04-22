package com.shannontheoret.machikoro;

import java.util.EnumMap;
import java.util.Map;

public class BuyingDecision {
    private final Landmark landmarkToPurchase;
    private final Card cardToPurchase;
    private Double averageBenefitPerRound = 0.0;
    private Map<StrategyName, Double> strategicValue = new EnumMap<>(StrategyName.class);
    private Double compositeScore = 0.0;

    private BuyingDecision(Landmark landmarkToPurchase, Card cardToPurchase) {
        this.landmarkToPurchase = landmarkToPurchase;
        this.cardToPurchase = cardToPurchase;
    }

    public BuyingDecision(Landmark landmarkToPurchase) {
        this.landmarkToPurchase = landmarkToPurchase;
        this.cardToPurchase = null;
    }

    public BuyingDecision(Card cardToPurchase) {
        this.cardToPurchase = cardToPurchase;
        this.landmarkToPurchase = null;
    }

    public static BuyingDecision endTurn() {
        return new BuyingDecision(null, null);
    }

    public static BuyingDecision buyLandmark(Landmark landmark) {
        return new BuyingDecision(landmark, null);
    }

    public static BuyingDecision buyCard(Card card) {
        return new BuyingDecision(null, card);
    }

    public Landmark getLandmarkToPurchase() {
        return landmarkToPurchase;
    }

    public Card getCardToPurchase() {
        return cardToPurchase;
    }

    public Integer getCost() {
        if (isBuyingCard()) {
            return cardToPurchase.getCost();
        } else if (isBuyingLandmark()) {
            return landmarkToPurchase.getCost();
        } else {
            return 0;
        }
    }

    public boolean isEndTurn() {
        return landmarkToPurchase == null && cardToPurchase == null;
    }

    public boolean isBuyingLandmark() {
        return landmarkToPurchase != null;
    }

    public boolean isBuyingCard() {
        return cardToPurchase != null;
    }

    public Double getAverageBenefitPerRound() {
        return averageBenefitPerRound;
    }

    public void setAverageBenefitPerRound(Double averageBenefitPerRound) {
        this.averageBenefitPerRound = averageBenefitPerRound;
    }

    public Double getProgressValue() {
        if (isBuyingCard()) {
            return 0.0;
        } else {
            return landmarkToPurchase.getProgress();
        }
    }

    public Map<StrategyName, Double> getStrategicValue() {
        return strategicValue;
    }

    public void setStrategicValue(Map<StrategyName, Double> strategicValue) {
        this.strategicValue = strategicValue;
    }

    public Double getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(Double compositeScore) {
        this.compositeScore = compositeScore;
    }
}