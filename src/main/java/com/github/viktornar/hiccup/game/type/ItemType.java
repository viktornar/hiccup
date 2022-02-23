package com.github.viktornar.hiccup.game.type;

import lombok.Getter;

public enum ItemType {
    HEALING_POTION("hpot");

    @Getter
    private final String id;

    ItemType(String id) {
        this.id = id;
    }
}
