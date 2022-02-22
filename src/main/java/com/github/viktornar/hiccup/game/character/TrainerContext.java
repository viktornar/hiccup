package com.github.viktornar.hiccup.game.character;

import lombok.Data;

@Data
public class TrainerContext {
    private String gameId;
    private int level = 0;
    private int gold = 0;
    private int score = 0;
    private int turn = 0;
    private int people = 0;
    private int state = 0;
    private int underworld = 0;
    private boolean gameEnd = true;
}
