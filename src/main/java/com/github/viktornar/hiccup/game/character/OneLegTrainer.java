package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.mapper.DtoToTrainerContextMapper;
import com.github.viktornar.hiccup.game.type.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class OneLegTrainer implements Trainer {
    private static final int MAX_TURNS = 100;
    private static final TrainerActions.State<TrainerContext, Event> IDLE =
            new TrainerActions.State<>(Event.IDLE.name());
    private static final TrainerActions.State<TrainerContext, Event> START =
            new TrainerActions.State<>(Event.START.name());
    private static final TrainerActions.State<TrainerContext, Event> REGISTER =
            new TrainerActions.State<>(Event.REGISTER.name());
    private static final TrainerActions.State<TrainerContext, Event> INVESTIGATE =
            new TrainerActions.State<>(Event.INVESTIGATE.name());
    private final APIClient apiClient;
    private final TrainerContext context;
    private final TrainerActions<TrainerContext, Event> oneLegTrainerActions;

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

        // While adventurer have what to do, try to train dragon
        do {
            initializeTraining();
        } while (!IDLE.equals(oneLegTrainerActions.getState()));

        // Register flows that can have tangled connection between each other :D
        registerGame();
        investigate();

        // Start the game
        oneLegTrainerActions.accept(Event.START);
    }

    @Override
    public TrainerContext getContext() {
        return context;
    }

    protected void registerGame() {
        REGISTER.onTransition((ctx, state) -> {
            if (!StringUtils.hasText(ctx.getGameId())) {
                var game = apiClient.startGame();
                var newCtx = DtoToTrainerContextMapper.INSTANCE.gameToContext(game);
                ctx.from(newCtx);
            }

            oneLegTrainerActions.accept(Event.INVESTIGATE);
        }).target(Event.INVESTIGATE, INVESTIGATE);
    }

    protected void initializeTraining() {
        START.onTransition((ctx, state) -> {
            if (ctx.getTurn() == MAX_TURNS) {
                log.info("Max turns limit reached. Terminating adventure.");
                START.target(Event.IDLE, IDLE);
                oneLegTrainerActions.accept(Event.IDLE);
            } else {
                oneLegTrainerActions.accept(Event.REGISTER);
            }
        }).target(Event.REGISTER, REGISTER);
    }

    private void investigate() {
        INVESTIGATE.onTransition((ctx, state) -> {
            log.info("Increase turn by one. Current turn {}", ctx.getTurn());
            ctx.setTurn(ctx.getTurn() + 1);
            oneLegTrainerActions.accept(Event.START);
        }).target(Event.START, START);
    }
}
