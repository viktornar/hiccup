package com.github.viktornar.hiccup.game.type;

@SuppressWarnings({ "java:S6205" })
public enum Probability {
    DANGEROUS,
    WALK_IN_PARK,
    QUITE_LIKELY,
    SURE_THING,
    PIECE_OF_CAKE;

    public static Probability of(String probabilityAsText) {
        switch (probabilityAsText) {
            case "Walk in the park" -> {
                return Probability.WALK_IN_PARK;
            }
            case "Quite likely" -> {
                return Probability.QUITE_LIKELY;
            }
            case "Sure thing" -> {
                return Probability.SURE_THING;
            }
            case "Piece of cake" -> {
                return Probability.PIECE_OF_CAKE;
            }
            default -> {
                return Probability.DANGEROUS;
            }
        }
    }
}
