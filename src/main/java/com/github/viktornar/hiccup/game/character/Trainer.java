package com.github.viktornar.hiccup.game.character;

public interface Trainer {
    void startAdventure(int maxTurn);
    TrainerContext getContext();
}
