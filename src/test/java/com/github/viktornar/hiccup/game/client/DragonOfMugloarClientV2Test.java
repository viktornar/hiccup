package com.github.viktornar.hiccup.game.client;

import com.github.viktornar.hiccup.HiccupProperties;
import com.github.viktornar.hiccup.game.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DragonOfMugloarClientV2Test {
    private DragonOfMugloarClientV2 dragonOfMugloarClientV2;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        HiccupProperties properties = mock(HiccupProperties.class);
        HiccupProperties.GameService gameService = new HiccupProperties.GameService();
        gameService.setBaseUrlForAPIv2("https://dragonsofmugloar.com");
        when(properties.getGameService()).thenReturn(gameService);
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.rootUri(any(String.class))).thenReturn(restTemplateBuilder);
        restTemplate = mock(RestTemplate.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        dragonOfMugloarClientV2 = new DragonOfMugloarClientV2(properties, restTemplateBuilder);
    }

    @Test
    void should_have_injected_properties_that_set_correct_base_api_url() {
        assertEquals("https://dragonsofmugloar.com", dragonOfMugloarClientV2.getBaseUrl());
    }

    @Test
    void should_start_a_new_game() {
        var game = new Game() {{
            setGameId("ZwU2VPGj");
            setGold(0);
            setHighScore(0);
            setScore(0);
            setLevel(0);
            setLives(3);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/game/start",
                null,
                Game.class)
        ).thenReturn(game);

        var response = dragonOfMugloarClientV2.startGame();
        assertEquals(game, response);
    }

    @Test
    void should_run_investigation() {
        var reputation = new Reputation() {{
            setPeople(0);
            setState(0);
            setUnderworld(0);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/investigate/reputation",
                null,
                Reputation.class)
        ).thenReturn(reputation);

        var response = dragonOfMugloarClientV2.investigateReputation("ZwU2VPGj");
        assertEquals(reputation, response);
    }

    @Test
    void should_get_all_quests() {
        var quest = new Quest() {{
            setAdId("ZtoUbmH1");
            setMessage("Help Shikoba Andrews to clean their potatoes");
            setExpiresIn(6);
            setProbability("Piece of cake");
            setReward(1);
        }};

        var quests = List.of(quest);

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{quest});

        var response = dragonOfMugloarClientV2.getAllQuests("ZwU2VPGj");
        assertEquals(quests, response);
    }

    @Test
    void should_solve_a_quest() {
        var reward = new Reward() {{
            setGold(1);
            setSuccess(true);
            setLives(3);
            setGold(1);
            setScore(1);
            setHighScore(0);
            setTurn(2);
            setMessage("You successfully solved the mission!");
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/solve/ZtoUbmH1",
                null,
                Reward.class)
        ).thenReturn(reward);
        var response = dragonOfMugloarClientV2.trySolveQuest("ZwU2VPGj", "ZtoUbmH1");
        assertEquals(reward, response);
    }

    @Test
    void should_get_list_of_items() {
        var item = new Item() {{
            setId("5CGAJ6D1");
            setCost(5);
            setName("Sword");
        }};

        var items = List.of(item);
        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        var response = dragonOfMugloarClientV2.getAllItems("ZwU2VPGj");
        assertEquals(items, response);
    }

    @Test
    void should_buy_item() {
        var basket = new Basket() {{
            setShoppingSuccess(true);
            setGold(5);
            setLives(3);
            setLevel(3);
            setTurn(3);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop/buy/5CGAJ6D1",
                null,
                Basket.class)
        ).thenReturn(basket);

        var response = dragonOfMugloarClientV2.tryPurchaseItem("ZwU2VPGj", "5CGAJ6D1");
        assertEquals(basket, response);
    }
}