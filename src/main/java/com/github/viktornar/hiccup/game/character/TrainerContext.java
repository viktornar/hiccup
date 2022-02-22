package com.github.viktornar.hiccup.game.character;

import lombok.Data;

@Data
public class TrainerContext {
    private String gameId;
    private Integer level = 0;
    private Integer gold = 0;
    private Integer score = 0;
    private Integer turn = 0;
    private Integer people = 0;
    private Integer state = 0;
    private Integer underworld = 0;
    private Boolean gameEnd = true;
    private Integer maxTurn = 0;
}
