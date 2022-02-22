package com.github.viktornar.hiccup.game.data;

import lombok.Data;

@Data
public class Game {
    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
}
