package com.github.viktornar.hiccup.game.dto;

import lombok.Data;

@Data
public class Game {
    private String gameId;
    private Integer lives;
    private Integer gold;
    private Integer level;
    private Integer score;
    private Integer highScore;
}
