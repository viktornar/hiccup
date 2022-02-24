package com.github.viktornar.hiccup.game.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProbabilityTypeTest {
    @Test
    void should_build_probability_from_text() {
        assertEquals(ProbabilityType.GAMBLE, ProbabilityType.of("Gamble"));
        assertEquals(ProbabilityType.RISKY, ProbabilityType.of("Risky"));
        assertEquals(ProbabilityType.WALK_IN_PARK, ProbabilityType.of("Walk in the park"));
        assertEquals(ProbabilityType.QUITE_LIKELY, ProbabilityType.of("Quite likely"));
        assertEquals(ProbabilityType.SURE_THING, ProbabilityType.of("Sure thing"));
        assertEquals(ProbabilityType.PIECE_OF_CAKE, ProbabilityType.of("Piece of cake"));
        assertEquals(ProbabilityType.IMPOSSIBLE, ProbabilityType.of("Impossible"));
        assertEquals(ProbabilityType.IMPOSSIBLE, ProbabilityType.of("some"));
        assertNotEquals(ProbabilityType.PIECE_OF_CAKE, ProbabilityType.of("Risky"));
    }
}