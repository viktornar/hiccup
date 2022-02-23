package com.github.viktornar.hiccup.game.type;

@SuppressWarnings({ "java:S6205" })
public enum ProbabilityType {
    IMPOSSIBLE,
    WALK_IN_PARK,
    QUITE_LIKELY,
    SURE_THING,
    RISKY,
    PLAYING_WITH_FIRE,
    PIECE_OF_CAKE;

    public static ProbabilityType of(String probabilityAsText) {
        switch (probabilityAsText) {
            case "Risky" -> {
                return ProbabilityType.RISKY;
            }
            case "Playing with fire" -> {
                return ProbabilityType.PLAYING_WITH_FIRE;
            }
            case "Walk in the park" -> {
                return ProbabilityType.WALK_IN_PARK;
            }
            case "Quite likely" -> {
                return ProbabilityType.QUITE_LIKELY;
            }
            case "Sure thing" -> {
                return ProbabilityType.SURE_THING;
            }
            case "Piece of cake" -> {
                return ProbabilityType.PIECE_OF_CAKE;
            }
            default -> {
                return ProbabilityType.IMPOSSIBLE;
            }
        }
    }
}
