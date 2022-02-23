package com.github.viktornar.hiccup.game.mapper;

import com.github.viktornar.hiccup.game.data.Basket;
import com.github.viktornar.hiccup.game.data.Game;
import com.github.viktornar.hiccup.game.data.Reputation;
import com.github.viktornar.hiccup.game.data.Reward;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataToTrainerContextMapperTest {
    @Test
    void should_map_game_to_context() {
        var game = new Game() {{
            setGameId("ZwU2VPGj");
            setGold(1);
            setHighScore(6);
            setScore(1);
            setLevel(1);
            setLives(3);
        }};

        var ctx = DataToTrainerContextMapper.INSTANCE.gameToContext(game);

        assertEquals(game.getGameId(), ctx.getGameId());
        assertEquals(game.getGold(), ctx.getGold());
        assertEquals(game.getLives(), ctx.getLives());
    }

    @Test
    void should_map_reward_to_context() {
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

        var ctx = DataToTrainerContextMapper.INSTANCE.rewardToContext(reward);

        assertEquals(reward.getGold(), ctx.getGold());
        assertEquals(reward.getLives(), ctx.getLives());
        assertEquals(reward.getTurn(), ctx.getTurn());
    }

    @Test
    void should_map_basket_to_context() {
        var basket = new Basket() {{
            setGold(30);
            setLives(3);
            setTurn(6);
            setTurn(5);
        }};

        var ctx = DataToTrainerContextMapper.INSTANCE.basketToContext(basket);

        assertEquals(basket.getGold(), ctx.getGold());
        assertEquals(basket.getLives(), ctx.getLives());
        assertEquals(basket.getTurn(), ctx.getTurn());
    }

    @Test
    void should_map_reputation_to_ctx() {
        var reputation = new Reputation() {{
            setPeople(3);
            setState(1);
            setUnderworld(5);
        }};

        var ctx = DataToTrainerContextMapper.INSTANCE.reputationToContext(reputation);

        assertEquals(reputation.getPeople(), ctx.getPeople());
        assertEquals(reputation.getState(), ctx.getState());
        assertEquals(reputation.getUnderworld(), ctx.getUnderworld());
    }
}
