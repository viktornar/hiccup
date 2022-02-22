package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.type.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OneLegTrainer implements Trainer {
    private static final int MAX_TURNS = 100;

    private final APIClient apiClient;
    private final TrainerContext context;
    private final TrainerActions<TrainerContext, Event> oneLegTrainerActions;

    private static final TrainerActions.State<TrainerContext, Event> IDLE =
            new TrainerActions.State<>(Event.IDLE.name());
    private static final TrainerActions.State<TrainerContext, Event> START =
            new TrainerActions.State<>(Event.START.name());
    private static final TrainerActions.State<TrainerContext, Event> INVESTIGATE =
            new TrainerActions.State<>(Event.INVESTIGATE.name());

    public OneLegTrainer(APIClient apiClient) {
        this.apiClient = apiClient;
        context = new TrainerContext();
        oneLegTrainerActions = new TrainerActions<>(context, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
    }

    @Override
    public void startAdventure() {
        IDLE.target(Event.START, START);

        // While adventure have what to do, try to train dragon
        do {
            START.onTransition((ctx, state) -> {
                if (ctx.getTurn() == MAX_TURNS) {
                    log.info("Max turns limit reached. Terminating adventure.");
                    START.target(Event.IDLE, IDLE);
                    oneLegTrainerActions.accept(Event.IDLE);
                } else {
                    oneLegTrainerActions.accept(Event.INVESTIGATE);
                }
            }).target(Event.INVESTIGATE, INVESTIGATE);
        } while (!IDLE.equals(oneLegTrainerActions.getState()));

        setInvestigationFlow();
        // Start the game
        oneLegTrainerActions.accept(Event.START);
    }

    private void setInvestigationFlow() {
        INVESTIGATE.onTransition((ctx, state) -> {
            log.info("Increase turn by one. Current turn {}", ctx.getTurn());
            ctx.setTurn(ctx.getTurn() + 1);
            oneLegTrainerActions.accept(Event.START);
        }).target(Event.START, START);
    }
}
