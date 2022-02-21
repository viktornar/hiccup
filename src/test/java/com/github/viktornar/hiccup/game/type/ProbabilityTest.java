package com.github.viktornar.hiccup.game.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTest {
    @Test
    void should_build_probability_from_text() {
        assertEquals(Probability.UNKNOWN, Probability.of(Probability.UNKNOWN.getProbabilityAsText()));
        assertEquals(Probability.LOW, Probability.of(Probability.LOW.getProbabilityAsText()));
        assertEquals(Probability.MEDIUM, Probability.of(Probability.MEDIUM.getProbabilityAsText()));
        assertEquals(Probability.HIGH, Probability.of(Probability.HIGH.getProbabilityAsText()));
        assertEquals(Probability.HIGHEST, Probability.of(Probability.HIGHEST.getProbabilityAsText()));
    }
}