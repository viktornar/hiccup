package com.github.viktornar.hiccup.game.data;

import lombok.Data;

@Data
public class Reward {
    private boolean success;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private String message;
}
