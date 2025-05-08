package com.shannontheoret.machikoro.utilities;

import com.shannontheoret.machikoro.Card;
import com.shannontheoret.machikoro.CardCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Map;
import java.util.Set;

public class PlayerStockUtilitiesTest {
    private Map<Card, Integer> playerStockOne = Map.of(
            Card.WHEAT, 2,
            Card.RANCH, 1,
            Card.BAKERY, 1,
            Card.CAFE, 3,
            Card.CONVENIENCE_STORE, 2,
            Card.MINE, 3,
            Card.APPLE_ORCHARD, 2,
            Card.FAMILY_RESTAURANT, 4,
            Card.FRUIT_AND_VEGETABLE_GARDEN, 1
    );

    private Map<Card, Integer> playerStockTwo = Map.of(
            Card.WHEAT, 1,
            Card.BAKERY, 1,
            Card.FOREST, 2,
            Card.STADIUM, 1,
            Card.TV_STATION, 1,
            Card.CHEESE_FACTORY, 1,
            Card.FURNITURE_FACTORY, 1
    );

    @Test
    public void getCountOfCardsInCategory_returns4GrainCards_playerStockOne() {
        assertEquals(4, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.GRAIN));
    }

    @Test
    public void getCountOfCardsInCategory_returns3GearCards_playerStockOne() {
        assertEquals(3, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.GEAR));
    }

    @Test
    public void getCountOfCardsInCategory_returns1CowCard_playerStockOne() {
        assertEquals(1, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.COW));
    }

    @Test
    public void getCountOfCardsInCategory_returns0PurpleCards_playerStockOne() {
        assertEquals(0, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.PURPLE));
    }

    @Test
    public void getCountOfCardsInCategory_returns3StoreCards_playerStockOne() {
        assertEquals(3, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.STORE));
    }

    @Test
    public void getCountOfCardsInCategory_returns7CupCards_playerStockOne() {
        assertEquals(7, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.CUP));
    }

    @Test
    public void getCountOfCardsInCategory_returns1OtherCard_playerStockOne() {
        assertEquals(1, PlayerStockUtilities.getCountOfCardsInCategory(playerStockOne, CardCategory.OTHER));
    }

    @Test
    public void getCountOfCardsInCategory_returns1GrainCard_playerStockTwo() {
        assertEquals(1, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.GRAIN));
    }

    @Test
    public void getCountOfCardsInCategory_returns2GearCards_playerStockTwo() {
        assertEquals(2, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.GEAR));
    }

    @Test
    public void getCountOfCardsInCategory_returns0CowCards_playerStockTwo() {
        assertEquals(0, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.COW));
    }

    @Test
    public void getCountOfCardsInCategory_returns2PurpleCards_playerStockTwo() {
        assertEquals(2, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.PURPLE));
    }

    @Test
    public void getCountOfCardsInCategory_returns1StoreCard_playerStockTwo() {
        assertEquals(1, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.STORE));
    }

    @Test
    public void getCountOfCardsInCategory_returns0CupCards_playerStockTwo() {
        assertEquals(0, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.CUP));
    }

    @Test
    public void getCountOfCardsInCategory_returns2OtherCards_playerStockTwo() {
        assertEquals(2, PlayerStockUtilities.getCountOfCardsInCategory(playerStockTwo, CardCategory.OTHER));
    }

    @Test
    public void getRedCardsForRoll_returnsEmptySetForRoll2() {
        assertEquals(Set.of(), PlayerStockUtilities.getRedCardsForRoll(playerStockOne, 2));
    }

    @Test
    public void getRedCardsForRoll_returnsCafeForRoll3() {
        assertEquals(Set.of(Card.CAFE), PlayerStockUtilities.getRedCardsForRoll(playerStockOne, 3));
    }

    @Test
    public void getRedCardsForRoll_returnsFamilyRestaurantForRoll9() {
        assertEquals(Set.of(Card.FAMILY_RESTAURANT), PlayerStockUtilities.getRedCardsForRoll(playerStockOne, 9));
    }

    @Test
    public void getRedCardsForRoll_returnsFamilyRestaurantForRoll10() {
        assertEquals(Set.of(Card.FAMILY_RESTAURANT), PlayerStockUtilities.getRedCardsForRoll(playerStockOne, 10));
    }

    @Test
    public void getRedCardsForRoll_returnsEmptySet_playerStockTwo() {
        assertEquals(Set.of(), PlayerStockUtilities.getRedCardsForRoll(playerStockTwo, 3));
    }

    @Test
    public void getBlueCardsForRoll_returnsWheatForRoll1() {
        assertEquals(Set.of(Card.WHEAT), PlayerStockUtilities.getBlueCardsForRoll(playerStockOne, 1));
    }

    @Test
    public void getBlueCardsForRoll_returnsRanchForRoll2() {
        assertEquals(Set.of(Card.RANCH), PlayerStockUtilities.getBlueCardsForRoll(playerStockOne, 2));
    }

    @Test
    public void getBlueCardsForRoll_returnsMineForRoll9() {
        assertEquals(Set.of(Card.MINE), PlayerStockUtilities.getBlueCardsForRoll(playerStockOne, 9));
    }

    @Test
    public void getBlueCardsForRoll_returnsAppleOrchardForRoll10() {
        assertEquals(Set.of(Card.APPLE_ORCHARD), PlayerStockUtilities.getBlueCardsForRoll(playerStockOne, 10));
    }

    @Test
    public void getBlueCardsForRoll_returnsEmptyForRoll3() {
        assertEquals(Set.of(), PlayerStockUtilities.getBlueCardsForRoll(playerStockOne, 3));
    }

    @Test
    public void getBlueCardsForRoll_returnsForestForRoll5() {
        assertEquals(Set.of(Card.FOREST), PlayerStockUtilities.getBlueCardsForRoll(playerStockTwo, 5));
    }

    @Test
    public void getBlueCardsForRoll_returnsEmptyForRoll6() {
        assertEquals(Set.of(), PlayerStockUtilities.getBlueCardsForRoll(playerStockTwo, 6));
    }

    @Test
    public void getBlueCardsForRoll_returnsEmptyForRoll8() {
        assertEquals(Set.of(), PlayerStockUtilities.getBlueCardsForRoll(playerStockTwo, 8));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsRanchAndBakeryForRoll2() {
        assertEquals(Set.of(Card.RANCH, Card.BAKERY), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockOne, 2));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsBakeryForRoll3() {
        assertEquals(Set.of(Card.BAKERY), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockOne, 3));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsConvenienceStoreForRoll4() {
        assertEquals(Set.of(Card.CONVENIENCE_STORE), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockOne, 4));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsEmptySetForRoll5() {
        assertEquals(Set.of(), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockOne, 5));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsAppleOrchardForRoll10() {
        assertEquals(Set.of(Card.APPLE_ORCHARD), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockOne, 10));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsCheeseFactoryForRoll7() {
        assertEquals(Set.of(Card.CHEESE_FACTORY), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockTwo, 7));
    }

    @Test
    public void getGreenAndBlueCardsForRoll_returnsEmptySet_playerStockTwo_Roll6() {
        assertEquals(Set.of(), PlayerStockUtilities.getGreenAndBlueCardsForPlayerRoll(playerStockTwo, 6));
    }

}
