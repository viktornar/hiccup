package com.github.viktornar.hiccup.game.data;

import lombok.Data;

@Data
public class Basket {
    private boolean shoppingSuccess;
    private int gold;
    private int lives;
    private int level;
    private int turn;
}
