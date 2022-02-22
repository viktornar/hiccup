package com.github.viktornar.hiccup.game.type;

@SuppressWarnings({ "java:S6205" })
public enum Probability {
    UNKNOWN,
    VERY_HARD,
    HARD,
    TRICKY,
    NORMAL,
    EASY,
    VERY_EASY;

    public static Probability of(String probabilityAsText) {
        switch (probabilityAsText) {
            case "Hmm..." -> {
                return Probability.VERY_HARD;
            }
            case "Risky" -> {
                return Probability.HARD;
            }
            case "Walk in the park" -> {
                return Probability.TRICKY;
            }
            case "Quite likely" -> {
                return Probability.NORMAL;
            }
            case "Sure thing" -> {
                return Probability.EASY;
            }
            case "Piece of cake" -> {
                return Probability.VERY_EASY;
            }
            default -> {
                return Probability.UNKNOWN;
            }
        }
    }
}
