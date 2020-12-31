package main;

import java.util.HashMap;
import java.util.Map;

public class GameSingleton {
    private static GameSingleton gameSingleton = null;
    private Map<String, Game> map = new HashMap<>();

    public static GameSingleton getInstance() {
        if (gameSingleton == null) {
            gameSingleton = new GameSingleton();
        }
        return gameSingleton;
    }

    public Game getGame(String code) {
        if (!map.containsKey(code)) {
          Game game = new Game();
          game.setCode(code);
          map.put(code, game);
        }
        return map.get(code);
    }
}
