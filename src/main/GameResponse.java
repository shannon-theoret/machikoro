package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GameResponse {
    private String code;
    private PlayerResponse player1;
    private PlayerResponse player2;
    private PlayerResponse player3;
    private Map<Integer, Integer> stock;
    private Integer currentPlayerNumber;
    private PlayerResponse currentPlayer;
    private String step;
    private Dice recentRoll;

    public GameResponse(Game game) {
        code = game.getCode();
        player1 = new PlayerResponse(game.getPlayer1());
        player2 = new PlayerResponse(game.getPlayer2());
        player3 = new PlayerResponse(game.getPlayer3());
        setStock(game.getGameStock());
        currentPlayerNumber = game.getCurrentPlayerNumeric();
        currentPlayer = new PlayerResponse(game.getCurrentPlayer());
        step = game.getStep().getStepName();
        recentRoll = new Dice(game.getRecentRoll());
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public PlayerResponse getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = new PlayerResponse(player1);
    }

    public PlayerResponse getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = new PlayerResponse(player2);
    }

    public PlayerResponse getPlayer3() {
        return player3;
    }

    public void setPlayer3(Player player3) {
        this.player3 = new PlayerResponse(player3);
    }

    public Map<Integer, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Card, Integer> stock) {
        this.stock = new HashMap<>();
        for (Entry<Card, Integer> entry: stock.entrySet()) {
            this.stock.put(entry.getKey().ordinal(), entry.getValue());
        }
    }

    public Integer getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(Integer currentPlayerNumber) {
        this.currentPlayerNumber = currentPlayerNumber;
    }

    public PlayerResponse getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = new PlayerResponse(currentPlayer);
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Dice getRecentRoll() {
        return recentRoll;
    }

    public void setRecentRoll(Dice recentRoll) {
        this.recentRoll = recentRoll;
    }
}
