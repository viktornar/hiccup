package com.github.viktornar.hiccup.game.dto;

import lombok.Data;

@Data
public class Quest {
    private String adId;
    private String message;
    private int reward;
    private int expiresIn;
    private String probability;
}
