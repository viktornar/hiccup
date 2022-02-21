package com.github.viktornar.hiccup.dragon.dto;

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
