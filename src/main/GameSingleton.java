package main;

public class GameSingleton {
    private static GameSingleton gameSingleton = null;

    private Game game;

    public static GameSingleton getInstance() {
        if (gameSingleton == null) {
            gameSingleton = new GameSingleton();
        }
        return gameSingleton;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
