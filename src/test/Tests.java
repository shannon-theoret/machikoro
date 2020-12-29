package test;

import main.Card;
import main.Game;
import org.junit.Test;


public class Tests {

    @Test
    public void handleRollTest (){
        Game game = new Game();
        game.getPlayer1().getStock().addCard(Card.RANCH, 3);
        game.getPlayer1().getStock().addCard(Card.CONVENIENCE_STORE, 2);
        game.getPlayer1().getStock().addCard(Card.FOREST, 1);
        game.getPlayer1().getStock().addCard(Card.CHEESE_FACTORY, 4);
        game.getPlayer1().getStock().addCard(Card.CAFE, 2);
        game.getPlayer1().getStock().addCard(Card.MINE, 2);
        game.getPlayer1().getStock().addCard(Card.FRUIT_AND_VEGETABLE_GARDEN, 5);

        game.getPlayer2().getStock().addCard(Card.CONVENIENCE_STORE, 2);
        game.getPlayer2().getStock().addCard(Card.FOREST, 3);
        game.getPlayer2().getStock().addCard(Card.APPLE_ORCHARD);
        game.getPlayer2().getStock().addCard(Card.CAFE);

        game.getPlayer3().getStock().addCard(Card.WHEAT);
        game.getPlayer3().getStock().addCard(Card.CAFE, 1);
        game.getPlayer3().getStock().addCard(Card.STADIUM, 1);
        game.getPlayer3().setHasShoppingMall(true);

        game.getPlayer1().setCoins(5);
        game.getPlayer2().setCoins(0);
        game.getPlayer3().setCoins(3);

        game.handleRoll(1);
        assert game.getPlayer1().getCoins() == 6;
        assert game.getPlayer2().getCoins() == 1;
        assert game.getPlayer3().getCoins() == 5;

        game.handleRoll(2);
        assert game.getPlayer1().getCoins() == 10;
        assert game.getPlayer2().getCoins() == 1;
        assert game.getPlayer3().getCoins() == 5;

        game.handleRoll(3);
        assert game.getPlayer1().getCoins() == 8;
        assert game.getPlayer2().getCoins() == 2;
        assert game.getPlayer3().getCoins() == 7;

        game.getPlayer1().setCoins(0);

        game.handleRoll(3);
        assert game.getPlayer1().getCoins() == 1;
        assert game.getPlayer2().getCoins() == 2;
        assert game.getPlayer3().getCoins() == 7;

        game.setCurrentPlayer(game.getPlayer3());

        game.getPlayer2().setCoins(5);

        game.handleRoll(6);
        assert game.getPlayer1().getCoins() == 0;
        assert game.getPlayer2().getCoins() == 3;
        assert game.getPlayer3().getCoins() == 10;

        game.handleRoll(1);
        assert game.getPlayer1().getCoins() == 1;
        assert game.getPlayer2().getCoins() == 4;
        assert game.getPlayer3().getCoins() == 12;
    }

}
