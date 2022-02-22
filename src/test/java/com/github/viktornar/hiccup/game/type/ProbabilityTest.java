package com.github.viktornar.hiccup.game.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {
    @Test
    void should_build_probability_from_text() {
        assertEquals(Probability.UNKNOWN, Probability.of("Gamble"));
        assertEquals(Probability.VERY_HARD, Probability.of("Hmm..."));
        assertEquals(Probability.HARD, Probability.of("Risky"));
        assertEquals(Probability.TRICKY, Probability.of("Walk in the park"));
        assertEquals(Probability.NORMAL, Probability.of("Quite likely"));
        assertEquals(Probability.EASY, Probability.of("Sure thing"));
        assertEquals(Probability.VERY_EASY, Probability.of("Piece of cake"));
    }
}