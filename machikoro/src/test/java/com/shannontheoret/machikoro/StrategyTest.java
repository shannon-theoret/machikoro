package com.shannontheoret.machikoro;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StrategyTest {

    @Test
    public void rollSingle_lowOptimistLowAttack() {
        Map<StrategyName, Integer> strategyMap = getBaseStrategyMap();
        strategyMap.put(StrategyName.ATTACK_FOCUSED, 2);
        strategyMap.put(StrategyName.OPTIMIST, 2);
        Strategy strategy = new Strategy(strategyMap);
        assertTrue(strategy.rollSingle(2.0,1.5,4, 1.5, 0.0));
        assertFalse(strategy.rollSingle(3.0, 4.5, 5, 1.0, 4.0));
    }

    private Map<StrategyName, Integer> getBaseStrategyMap() {
        Map<StrategyName, Integer> strategyMap = new EnumMap<>(StrategyName.class);
        for (StrategyName strategy : StrategyName.values()) {
            strategyMap.put(strategy, 1);
        }
        return strategyMap;
    }


}
