package com.github.viktornar.hiccup.game.mapper;

import com.github.viktornar.hiccup.game.dto.Game;
import com.github.viktornar.hiccup.game.dto.Reward;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoToTrainerContextMapperTest {

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

        var ctx = DtoToTrainerContextMapper.INSTANCE.gameToContext(game);

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

        var ctx = DtoToTrainerContextMapper.INSTANCE.rewardToContext(reward);

        assertEquals(reward.getGold(), ctx.getGold());
        assertEquals(reward.getLives(), ctx.getLives());
        assertEquals(reward.getTurn(), ctx.getTurn());
    }
}