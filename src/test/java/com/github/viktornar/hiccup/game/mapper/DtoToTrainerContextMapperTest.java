package com.github.viktornar.hiccup.game.mapper;

import com.github.viktornar.hiccup.game.dto.Game;
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
}