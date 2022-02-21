package com.github.viktornar.hiccup.game.dto;

import lombok.Data;

@Data
public class Reward {
    private Boolean success;
    private Integer lives;
    private Integer gold;
    private Integer score;
    private Integer highScore;
    private Integer turn;
    private String message;
}
