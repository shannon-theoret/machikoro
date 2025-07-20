package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.StrategyName;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

@Component
public class GameUtilities {
    private static final Random RANDOM = new Random();

    public Integer generateRandomDieRoll() {
        return RANDOM.nextInt(6) + 1;
    }

    public String generateCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Integer STRING_LENGTH = 8;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static Map<StrategyName, Integer> generateReasonableStrategy() {
        Map<StrategyName, Integer> reasonableStrategy = new EnumMap<>(StrategyName.class);
        reasonableStrategy.put(StrategyName.SAVER, getStrategyDegree());
        reasonableStrategy.put(StrategyName.OPTIMIST, getStrategyDegree());
        reasonableStrategy.put(StrategyName.ATTACK_FOCUSED, getStrategyDegree());
        reasonableStrategy.put(StrategyName.CHEESE_FOCUSED, 0);
        reasonableStrategy.put(StrategyName.FACTORY_FOCUSED, 0);
        reasonableStrategy.put(StrategyName.FRUIT_AND_VEG_FOCUSED, 0);
        Integer choice = RANDOM.nextInt(4);
        switch (choice) {
            case 1:
                reasonableStrategy.put(StrategyName.CHEESE_FOCUSED, 1);
                break;
            case 2:
                reasonableStrategy.put(StrategyName.FACTORY_FOCUSED, 1);
                break;
            case 3:
                reasonableStrategy.put(StrategyName.FRUIT_AND_VEG_FOCUSED, 1);
                break;
            default:
                break;
        }
        return reasonableStrategy;
    }

    private static Integer getStrategyDegree() {
        return RANDOM.nextInt(0,4);
    }
}
