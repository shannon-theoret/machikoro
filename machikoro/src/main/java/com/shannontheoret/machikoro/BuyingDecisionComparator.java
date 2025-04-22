package com.shannontheoret.machikoro;

import java.util.Comparator;

public class BuyingDecisionComparator implements Comparator<BuyingDecision> {

    @Override
    public int compare(BuyingDecision a, BuyingDecision b) {
        int scoreComparison = Double.compare(b.getCompositeScore(), a.getCompositeScore());
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        return Integer.compare(a.getCost(), b.getCost());
    }
}
