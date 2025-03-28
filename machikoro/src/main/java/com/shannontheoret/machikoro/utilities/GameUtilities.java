package com.shannontheoret.machikoro.utilities;

import java.util.Random;

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
}
