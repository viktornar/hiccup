package com.github.viktornar.hiccup.game.type;

import lombok.Getter;

@SuppressWarnings({ "java:S6205" })
public enum Probability {
    UNKNOWN(""),
    LOW("Walk in the park"),
    MEDIUM("Quite likely"),
    HIGH("Sure thing"),
    HIGHEST("Piece of cake");

    @Getter
    private final String probabilityAsText;

    Probability(String probabilityAsText) {
        this.probabilityAsText = probabilityAsText;
    }

    static Probability of(String probabilityAsText) {
        switch (probabilityAsText) {
            case "Walk in the park" -> {
                return Probability.LOW;
            }
            case "Quite likely" -> {
                return Probability.MEDIUM;
            }
            case "Sure thing" -> {
                return Probability.HIGH;
            }
            case "Piece of cake" -> {
                return Probability.HIGHEST;
            }
            default -> {
                return Probability.UNKNOWN;
            }
        }
    }
}
