package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.HiccupProperties;
import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.client.DragonOfMugloarClientV2;
import com.github.viktornar.hiccup.game.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OneLegTrainerTest {
    private RestTemplate restTemplate;
    private Trainer oneLegTrainer;

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
        APIClient dragonOfMugloarClientV2 = new DragonOfMugloarClientV2(properties, restTemplateBuilder);
        oneLegTrainer = new OneLegTrainer(dragonOfMugloarClientV2);
    }

    @Test
    void try_to_accomplish_one_simple_turn() {
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

        var reputation = new Reputation() {{
            setPeople(1);
            setState(1);
            setUnderworld(1);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/investigate/reputation",
                null,
                Reputation.class)
        ).thenReturn(reputation);

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        var reward = new Reward() {{
            setGold(140);
            setSuccess(true);
            setLives(3);
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

        var item = new Item() {{
            setId("wc");
            setCost(60);
            setName("Sword");
        }};

        var items = List.of(item);
        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        var basket = new Basket() {{
            setShoppingSuccess(true);
            setGold(80);
            setLives(3);
            setLevel(0);
            setTurn(3);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop/buy/wc",
                null,
                Basket.class)
        ).thenReturn(basket);

        oneLegTrainer.startAdventure(3);
        var ctx = oneLegTrainer.getContext();

        assertEquals(80, ctx.getGold());
        assertEquals(1, ctx.getScore());
        assertEquals(3, ctx.getLives());
        assertEquals(1, ctx.getState());
        assertEquals(3, ctx.getTurn());
        assertEquals("wc", ctx.getPurchasedItems().get(0).getId());
        assertEquals(60, ctx.getPurchasedItems().get(0).getCost());
    }

    @Test
    void should_solve_dangerous_quest_in_one_simple_turn() {
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

        var reputation = new Reputation() {{
            setPeople(1);
            setState(1);
            setUnderworld(1);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/investigate/reputation",
                null,
                Reputation.class)
        ).thenReturn(reputation);

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        var reward = new Reward() {{
            setGold(140);
            setSuccess(true);
            setLives(3);
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

        var item = new Item() {{
            setId("wc");
            setCost(60);
            setName("Sword");
        }};

        var items = List.of(item);
        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        var basket = new Basket() {{
            setShoppingSuccess(true);
            setGold(80);
            setLives(3);
            setLevel(0);
            setTurn(3);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop/buy/wc",
                null,
                Basket.class)
        ).thenReturn(basket);

        oneLegTrainer.startAdventure(3);
        var ctx = oneLegTrainer.getContext();

        assertEquals(80, ctx.getGold());
        assertEquals(1, ctx.getScore());
        assertEquals(3, ctx.getLives());
        assertEquals(1, ctx.getState());
        assertEquals(3, ctx.getTurn());
        assertEquals("wc", ctx.getPurchasedItems().get(0).getId());
        assertEquals(60, ctx.getPurchasedItems().get(0).getCost());
    }

    @Test
    void should_die_on_dangerous_mission() {
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

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        var reward = new Reward() {{
            setGold(0);
            setSuccess(false);
            setLives(2);
            setScore(1);
            setHighScore(0);
            setTurn(2);
            setMessage("You failed!!!");
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/solve/ZtoUbmH1",
                null,
                Reward.class)
        ).thenReturn(reward);

        var item = new Item() {{
            setId("wc");
            setCost(1000);
            setName("Sword");
        }};

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        oneLegTrainer.startAdventure(2);
        var ctx = oneLegTrainer.getContext();

        assertEquals(0, ctx.getGold());
        assertEquals(1, ctx.getScore());
        assertEquals(2, ctx.getLives());
        assertEquals(0, ctx.getState());
        assertEquals(2, ctx.getTurn());
        assertEquals(0, ctx.getPurchasedItems().size());
    }

    @Test
    void should_heal_after_death() {
        var game = new Game() {{
            setGameId("ZwU2VPGj");
            setGold(50);
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

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        var reward = new Reward() {{
            setGold(50);
            setSuccess(false);
            setLives(2);
            setScore(1);
            setHighScore(0);
            setTurn(2);
            setMessage("You failed!!!");
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/solve/ZtoUbmH1",
                null,
                Reward.class)
        ).thenReturn(reward);

        var item = new Item() {{
            setId("hpot");
            setCost(50);
            setName("Health potion");
        }};

        var items = List.of(item);
        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        var basket = new Basket() {{
            setShoppingSuccess(true);
            setGold(0);
            setLives(3);
            setLevel(0);
            setTurn(3);
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop/buy/hpot",
                null,
                Basket.class)
        ).thenReturn(basket);

        oneLegTrainer.startAdventure(2);
        var ctx = oneLegTrainer.getContext();

        assertEquals(0, ctx.getGold());
        assertEquals(1, ctx.getScore());
        assertEquals(3, ctx.getLives());
        assertEquals(0, ctx.getState());
        assertEquals(3, ctx.getTurn());
        assertEquals(0, ctx.getPurchasedItems().size());
    }

    @Test
    void should_just_die() {
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

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        var reward = new Reward() {{
            setGold(0);
            setSuccess(false);
            setLives(0);
            setScore(1);
            setHighScore(0);
            setTurn(2);
            setMessage("You failed!!!");
        }};

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/solve/ZtoUbmH1",
                null,
                Reward.class)
        ).thenReturn(reward);

        var item = new Item() {{
            setId("wc");
            setCost(1000);
            setName("Sword");
        }};

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        oneLegTrainer.startAdventure(2);
        var ctx = oneLegTrainer.getContext();

        assertEquals(0, ctx.getGold());
        assertEquals(1, ctx.getScore());
        assertEquals(0, ctx.getLives());
        assertEquals(0, ctx.getState());
        assertEquals(2, ctx.getTurn());
        assertEquals(0, ctx.getPurchasedItems().size());
    }

    @Test
    void should_handle_unexpected_exception() {
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

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/messages",
                Quest[].class)
        ).thenReturn(new Quest[]{
                new Quest() {{
                    setAdId("ZtoUbmH1");
                    setMessage("Help defending field in Newheart from the intruders");
                    setExpiresIn(6);
                    setProbability("Risky");
                    setReward(140);
                }}
        });

        when(restTemplate.postForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/solve/ZtoUbmH1",
                null,
                Reward.class)
        ).thenThrow(new ResourceAccessException("Some unexpected exception"));

        var item = new Item() {{
            setId("wc");
            setCost(1000);
            setName("Sword");
        }};

        when(restTemplate.getForObject(
                "https://dragonsofmugloar.com/api/v2/ZwU2VPGj/shop",
                Item[].class)
        ).thenReturn(new Item[]{item});

        oneLegTrainer.startAdventure(2);
        var ctx = oneLegTrainer.getContext();

        assertTrue(ctx.isGameOver());
    }
}