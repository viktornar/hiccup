package com.github.viktornar.hiccup.game.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {
    @Test
    void should_build_probability_from_text() {
        assertEquals(Probability.DANGEROUS, Probability.of("Gamble"));
        assertEquals(Probability.DANGEROUS, Probability.of("Risky"));
        assertEquals(Probability.WALK_IN_PARK, Probability.of("Walk in the park"));
        assertEquals(Probability.QUITE_LIKELY, Probability.of("Quite likely"));
        assertEquals(Probability.SURE_THING, Probability.of("Sure thing"));
        assertEquals(Probability.PIECE_OF_CAKE, Probability.of("Piece of cake"));
    }
}