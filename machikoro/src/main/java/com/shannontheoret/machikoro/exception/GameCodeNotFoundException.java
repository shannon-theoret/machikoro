package com.shannontheoret.machikoro.exception;

public class GameCodeNotFoundException extends GameException {

    public GameCodeNotFoundException(String gameCode) {
        super("Invalid game code: " + gameCode);
    }
}