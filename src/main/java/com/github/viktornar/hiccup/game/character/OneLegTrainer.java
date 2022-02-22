package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.data.Reward;
import com.github.viktornar.hiccup.game.mapper.DtoToTrainerContextMapper;
import com.github.viktornar.hiccup.game.type.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class OneLegTrainer implements Trainer {
    private static final int MAX_TURNS = 1;
    private static final String STEP_CONTEXT_LOG_TEXT = "Step {}, context {}";

    private static final TrainerActions.State<TrainerContext, Event> IDLE =
            new TrainerActions.State<>(Event.IDLE.name());
    private static final TrainerActions.State<TrainerContext, Event> START =
            new TrainerActions.State<>(Event.START.name());
    private static final TrainerActions.State<TrainerContext, Event> REGISTER =
            new TrainerActions.State<>(Event.REGISTER.name());
    private static final TrainerActions.State<TrainerContext, Event> INVESTIGATE =
            new TrainerActions.State<>(Event.INVESTIGATE.name());
    private static final TrainerActions.State<TrainerContext, Event> GET_QUESTS =
            new TrainerActions.State<>(Event.GET_QUESTS.name());
    private static final TrainerActions.State<TrainerContext, Event> SOLVE_SIMPLE_QUESTS =
            new TrainerActions.State<>(Event.SOLVE_SIMPLE_QUESTS.name());

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

        // Adventurer is busy by doing dragon training
        do {
            doTraining();
        } while (!IDLE.equals(oneLegTrainerActions.getState()));

        // Register flows that can have tangled connection between each other :D
        initGame();
        initQuests();
        initInvestigate();
        initSimpleQuestsSolver();

        // Start the game
        oneLegTrainerActions.accept(Event.START);
    }

    @Override
    public TrainerContext getContext() {
        return context;
    }

    protected void doTraining() {
        START.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, Event.START.name(), ctx);

            if (ctx.getTurn() >= MAX_TURNS) {
                log.info("Max turns limit reached. Terminating adventure.");
                START.target(Event.IDLE, IDLE);
                oneLegTrainerActions.accept(Event.IDLE);
            } else {
                oneLegTrainerActions.accept(Event.REGISTER);
            }
        }).target(Event.REGISTER, REGISTER);
    }

    protected void initGame() {
        REGISTER.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, Event.REGISTER.name(), ctx);
            if (!StringUtils.hasText(ctx.getGameId())) {
                var game = apiClient.startGame();
                var newCtx = DtoToTrainerContextMapper.INSTANCE.gameToContext(game);
                ctx.from(newCtx);
            }
            // I'm brave trainer. Let's start solving quests.
            oneLegTrainerActions.accept(Event.GET_QUESTS);
        }).target(Event.GET_QUESTS, GET_QUESTS);
    }

    private void initInvestigate() {
        INVESTIGATE.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, Event.INVESTIGATE.name(), ctx);
            log.info("Increase turn by one. Current turn {}", ctx.getTurn());
            ctx.setTurn(ctx.getTurn() + 1);
            // Start over again
            oneLegTrainerActions.accept(Event.START);
        }).target(Event.START, START);
    }

    private void initQuests() {
        GET_QUESTS.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, Event.GET_QUESTS.name(), ctx);
            var quests = apiClient.getAllQuests(ctx.getGameId());
            log.info("Receive quests to solve:");
            quests.forEach(q -> log.info("\tquest: {}", q));
            ctx.setQuests(quests);
            // Try to solve only easy quests
            oneLegTrainerActions.accept(Event.SOLVE_SIMPLE_QUESTS);
        }).target(Event.SOLVE_SIMPLE_QUESTS, SOLVE_SIMPLE_QUESTS);
    }

    private void initSimpleQuestsSolver() {
        SOLVE_SIMPLE_QUESTS.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, Event.SOLVE_SIMPLE_QUESTS.name(), ctx);
            var quest = QuestUtil.getEasiestQuest(ctx.getQuests());
            quest.ifPresent(value -> {
                Reward reward = apiClient.trySolveQuest(ctx.getGameId(), value.getAdId());
                var newCtx = DtoToTrainerContextMapper.INSTANCE.rewardToContext(reward);
                ctx.from(newCtx);
            });
            oneLegTrainerActions.accept(Event.INVESTIGATE);
        }).target(Event.INVESTIGATE, INVESTIGATE);
    }
}
