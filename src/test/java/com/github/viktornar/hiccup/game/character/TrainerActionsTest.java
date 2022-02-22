package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.type.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerActionsTest {
    private TrainerActions.State<TrainerContext, Event> IDLE;
    private TrainerActions.State<TrainerContext, Event> START;
    private TrainerActions.State<TrainerContext, Event> INVESTIGATE;
    private TrainerActions.State<TrainerContext, Event> END;

    @BeforeEach
    void setUp() {
        IDLE = new TrainerActions.State<>(Event.IDLE.name());
        START = new TrainerActions.State<>(Event.START.name());
        INVESTIGATE = new TrainerActions.State<>(Event.INVESTIGATE.name());
        END = new TrainerActions.State<>(Event.END.name());
    }

    @Test
    void should_switch_from_one_to_next_state_with_mutable_context() {
        var trainerContext = new TrainerContext();
        // Initialize trainer state machine
        TrainerActions<TrainerContext, Event> oneLegTrainerActions = new TrainerActions<>(trainerContext, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
        // Describe flow
        assertNull(trainerContext.getGameId());
        IDLE.target(Event.START, START);
        START.onTransition((ctx, state) -> {
            ctx.setGameId("ZwU2VPGj");
        }).target(Event.INVESTIGATE, INVESTIGATE);
        INVESTIGATE.target(Event.END, END);
        END.target(Event.IDLE, IDLE);
        // Start flow
        oneLegTrainerActions.accept(Event.START);
        assertEquals(START, oneLegTrainerActions.getState());
        oneLegTrainerActions.accept(Event.INVESTIGATE);
        assertEquals(INVESTIGATE, oneLegTrainerActions.getState());
        assertEquals("ZwU2VPGj", trainerContext.getGameId());
        oneLegTrainerActions.accept(Event.END);
        assertEquals(END, oneLegTrainerActions.getState());
        oneLegTrainerActions.accept(Event.IDLE);
        assertEquals(IDLE, oneLegTrainerActions.getState());
    }

    @Test
    void should_end_flow_without_investigation() {
        var trainerContext = new TrainerContext();
        // Initialize trainer state machine
        TrainerActions<TrainerContext, Event> oneLegTrainerActions = new TrainerActions<>(trainerContext, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
        IDLE.target(Event.START, START);
        START.onTransition((ctx, state) -> {
            ctx.setGameId("ZwU2VPGj");
            // Was not able to do something.
            // End flow by redefine and accept in step in transition.
            START.target(Event.END, END);
            oneLegTrainerActions.accept(Event.END);
        }).target(Event.INVESTIGATE, INVESTIGATE);
        INVESTIGATE.target(Event.END, END);
        // Describe flow
        assertEquals(IDLE, oneLegTrainerActions.getState());
        // Start flow
        oneLegTrainerActions.accept(Event.START);
        assertEquals(END, oneLegTrainerActions.getState());
        assertEquals("ZwU2VPGj", trainerContext.getGameId());
    }
}