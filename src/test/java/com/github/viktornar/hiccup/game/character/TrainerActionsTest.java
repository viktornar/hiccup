package com.github.viktornar.hiccup.game.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerActionsTest {
    private TrainerActions.State<TrainerContext, TrainerEvent> IDLE;
    private TrainerActions.State<TrainerContext, TrainerEvent> START;
    private TrainerActions.State<TrainerContext, TrainerEvent> INVESTIGATE;
    private TrainerActions.State<TrainerContext, TrainerEvent> END;

    @BeforeEach
    void setUp() {
        IDLE = new TrainerActions.State<>(TrainerEvent.IDLE.name());
        START = new TrainerActions.State<>(TrainerEvent.START.name());
        INVESTIGATE = new TrainerActions.State<>(TrainerEvent.INVESTIGATE.name());
        END = new TrainerActions.State<>(TrainerEvent.END.name());
    }

    @Test
    void should_switch_from_one_to_next_state_with_mutable_context() {
        var trainerContext = new TrainerContext();
        // Initialize trainer state machine
        TrainerActions<TrainerContext, TrainerEvent> oneLegTrainerActions = new TrainerActions<>(trainerContext, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
        // Describe flow
        assertNull(trainerContext.getGameId());
        IDLE.target(TrainerEvent.START, START);
        START.onTransition((ctx, state) -> {
            ctx.setGameId("ZwU2VPGj");
        }).target(TrainerEvent.INVESTIGATE, INVESTIGATE);
        INVESTIGATE.target(TrainerEvent.END, END);
        END.target(TrainerEvent.IDLE, IDLE);
        // Start flow
        oneLegTrainerActions.accept(TrainerEvent.START);
        assertEquals(START, oneLegTrainerActions.getState());
        oneLegTrainerActions.accept(TrainerEvent.INVESTIGATE);
        assertEquals(INVESTIGATE, oneLegTrainerActions.getState());
        assertEquals("ZwU2VPGj", trainerContext.getGameId());
        oneLegTrainerActions.accept(TrainerEvent.END);
        assertEquals(END, oneLegTrainerActions.getState());
        oneLegTrainerActions.accept(TrainerEvent.IDLE);
        assertEquals(IDLE, oneLegTrainerActions.getState());
    }

    @Test
    void should_end_flow_without_investigation() {
        var trainerContext = new TrainerContext();
        // Initialize trainer state machine
        TrainerActions<TrainerContext, TrainerEvent> oneLegTrainerActions = new TrainerActions<>(trainerContext, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
        IDLE.target(TrainerEvent.START, START);
        START.onTransition((ctx, state) -> {
            ctx.setGameId("ZwU2VPGj");
            // Was not able to do something.
            // End flow by redefine and accept in step in transition.
            START.target(TrainerEvent.END, END);
            oneLegTrainerActions.accept(TrainerEvent.END);
        }).target(TrainerEvent.INVESTIGATE, INVESTIGATE);
        INVESTIGATE.target(TrainerEvent.END, END);
        // Describe flow
        assertEquals(IDLE, oneLegTrainerActions.getState());
        // Start flow
        oneLegTrainerActions.accept(TrainerEvent.START);
        assertEquals(END, oneLegTrainerActions.getState());
        assertEquals("ZwU2VPGj", trainerContext.getGameId());
    }
}