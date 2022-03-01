package com.github.viktornar.hiccup.game.type;

import lombok.Getter;

import java.util.Arrays;

@SuppressWarnings({"java:S6205"})
public enum ProbabilityType {
    IMPOSSIBLE("Impossible"),
    WALK_IN_PARK("Walk in the park"),
    QUITE_LIKELY("Quite likely"),
    SURE_THING("Sure thing"),
    RISKY("Risky"),
    HMM("Hmm...."),
    GAMBLE("Gamble"),
    PLAYING_WITH_FIRE("Playing with fire"),
    RATHER_DETRIMENTAL("Rather detrimental"),
    PIECE_OF_CAKE("Piece of cake");

    @Getter
    private final String probability;

    ProbabilityType(String probabilityAsText) {
        this.probability = probabilityAsText;
    }

    public static ProbabilityType of(String probabilityAsText) {
        return Arrays.stream(ProbabilityType.values())
                .filter(p -> p.probability.equals(probabilityAsText))
                .findFirst()
                .orElse(ProbabilityType.IMPOSSIBLE);
    }
}
