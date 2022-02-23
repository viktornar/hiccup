package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.data.Reward;
import com.github.viktornar.hiccup.game.mapper.DtoToTrainerContextMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class OneLegTrainer implements Trainer {
    public static final int MAX_TURN = 100;
    private static final String STEP_CONTEXT_LOG_TEXT = "Step {}, context {}";

    private static final TrainerActions.State<TrainerContext, TrainerEvent> IDLE =
            new TrainerActions.State<>(TrainerEvent.IDLE.name());
    private static final TrainerActions.State<TrainerContext, TrainerEvent> START =
            new TrainerActions.State<>(TrainerEvent.START.name());
    private static final TrainerActions.State<TrainerContext, TrainerEvent> REGISTER =
            new TrainerActions.State<>(TrainerEvent.REGISTER.name());
    private static final TrainerActions.State<TrainerContext, TrainerEvent> INVESTIGATE =
            new TrainerActions.State<>(TrainerEvent.INVESTIGATE.name());
    private static final TrainerActions.State<TrainerContext, TrainerEvent> GET_QUESTS =
            new TrainerActions.State<>(TrainerEvent.GET_QUESTS.name());
    private static final TrainerActions.State<TrainerContext, TrainerEvent> SOLVE_SIMPLE_QUESTS =
            new TrainerActions.State<>(TrainerEvent.SOLVE_EASY_QUESTS.name());

    private final APIClient apiClient;
    private final TrainerContext context;
    private final TrainerActions<TrainerContext, TrainerEvent> oneLegTrainerActions;

    private int maxTurn = MAX_TURN;

    public OneLegTrainer(APIClient apiClient) {
        this.apiClient = apiClient;
        context = new TrainerContext();
        oneLegTrainerActions = new TrainerActions<>(context, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
    }

    @Override
    public void startAdventure(int maxTurn) {
        IDLE.target(TrainerEvent.START, START);
        this.maxTurn = maxTurn;

        // Adventurer is busy by doing dragon training
        do {
            doTraining();
        } while (!IDLE.equals(oneLegTrainerActions.getState()));

        // Register flows that can have tangled connection between each other :D
        initGame();
        initQuests();
        initInvestigate();
        initEasyQuestsSolver();
        // TODO: Implement flows
        // initNormalQuestsSolver();
        // initHardQuestsSolver();
        // initBuyItem();

        // Start the game
        oneLegTrainerActions.accept(TrainerEvent.START);
    }

    @Override
    public TrainerContext getContext() {
        return context;
    }

    protected void doTraining() {
        START.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.START.name(), ctx);

            if (ctx.getTurn() >= this.maxTurn) {
                log.info("Max turns limit reached. Terminating adventure.");
                START.target(TrainerEvent.IDLE, IDLE);
                oneLegTrainerActions.accept(TrainerEvent.IDLE);
            } else {
                oneLegTrainerActions.accept(TrainerEvent.REGISTER);
            }
        }).target(TrainerEvent.REGISTER, REGISTER);
    }

    protected void initGame() {
        REGISTER.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.REGISTER.name(), ctx);
            if (!StringUtils.hasText(ctx.getGameId())) {
                var game = apiClient.startGame();
                var newCtx = DtoToTrainerContextMapper.INSTANCE.gameToContext(game);
                ctx.from(newCtx);
            }
            // I'm brave trainer. Let's start solving quests.
            oneLegTrainerActions.accept(TrainerEvent.GET_QUESTS);
        }).target(TrainerEvent.GET_QUESTS, GET_QUESTS);
    }

    private void initInvestigate() {
        INVESTIGATE.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.INVESTIGATE.name(), ctx);
            log.info("Increase turn by one. Current turn {}", ctx.getTurn());
            ctx.setTurn(ctx.getTurn() + 1);
            ctx.setExpiresInCount(ctx.getExpiresInCount() + 1);
            // Start over again
            oneLegTrainerActions.accept(TrainerEvent.START);
        }).target(TrainerEvent.START, START);
    }

    private void initQuests() {
        GET_QUESTS.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.GET_QUESTS.name(), ctx);
            var quests = apiClient.getAllQuests(ctx.getGameId());
            log.info("Receive quests to solve:");
            quests.forEach(q -> log.info("\tquest: {}", q));
            ctx.setQuests(quests);
            ctx.setExpiresInCount(0);
            // Try to solve only easy quests
            oneLegTrainerActions.accept(TrainerEvent.SOLVE_EASY_QUESTS);
        }).target(TrainerEvent.SOLVE_EASY_QUESTS, SOLVE_SIMPLE_QUESTS);
    }

    private void initEasyQuestsSolver() {
        SOLVE_SIMPLE_QUESTS.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.SOLVE_EASY_QUESTS.name(), ctx);
            var quest = QuestsUtil.getEasiestQuest(ctx);
            quest.ifPresent(value -> {
                Reward reward = apiClient.trySolveQuest(ctx.getGameId(), value.getAdId());
                var newQuest = ctx.getQuests();
                // Remove quest. It doesn't exist anymore for trainer
                newQuest.remove(quest.get());
                var newCtx = DtoToTrainerContextMapper.INSTANCE.rewardToContext(reward);
                newCtx.setQuests(newQuest);
                ctx.from(newCtx);
                // Increase expire count for existing quests in list
                ctx.setExpiresInCount(ctx.getExpiresInCount() + 1);
            });
            oneLegTrainerActions.accept(TrainerEvent.INVESTIGATE);
        }).target(TrainerEvent.INVESTIGATE, INVESTIGATE);
    }
}
