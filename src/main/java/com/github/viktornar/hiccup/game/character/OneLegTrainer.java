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
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> START =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> REGISTER =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> INVESTIGATE =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> GET_QUESTS =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> SOLVE_SAFE_QUESTS =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> BUY_ITEM =
            new TrainerActions.State<>();
    public static final int HEAL_POTION_PRICE = 50;
    public static final int MAX_LIVES = 3;

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
        // Adventurer is busy by doing dragon training :)
        do {
            doTraining();
        } while (!IDLE.equals(oneLegTrainerActions.getState()));
        // Register flows that can have tangled connection between each other :D
        initGame();
        initQuests();
        initInvestigate();
        initSafeQuestsSolver();
        initBuyItem();
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
            var reputation = apiClient.investigateReputation(ctx.getGameId());
            var newCtx = DtoToTrainerContextMapper.INSTANCE.reputationToContext(reputation);
            ctx.from(newCtx);
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
            oneLegTrainerActions.accept(TrainerEvent.SOLVE_SAFE_QUESTS);
        }).target(TrainerEvent.SOLVE_SAFE_QUESTS, SOLVE_SAFE_QUESTS);
    }

    private void initSafeQuestsSolver() {
        SOLVE_SAFE_QUESTS.onTransition((ctx, state) -> {
            log.info(STEP_CONTEXT_LOG_TEXT, TrainerEvent.SOLVE_SAFE_QUESTS.name(), ctx);
            var quest = QuestsUtil.getSafeQuest(ctx);
            log.info("Will try to solve quest: {}", quest);
            quest.ifPresent(value -> {
                Reward reward = apiClient.trySolveQuest(ctx.getGameId(), value.getAdId());
                log.info("Reward (or not) from quest: {}", reward);
                var newQuest = ctx.getQuests();
                // Remove quest. It doesn't exist anymore for trainer
                newQuest.remove(quest.get());
                var newCtx = DtoToTrainerContextMapper.INSTANCE.rewardToContext(reward);
                newCtx.setQuests(newQuest);
                ctx.from(newCtx);
                // Increase expire count for existing quests in list
                ctx.setExpiresInCount(ctx.getExpiresInCount() + 1);
            });
            oneLegTrainerActions.accept(TrainerEvent.BUY_ITEM);
        }).target(TrainerEvent.BUY_ITEM, BUY_ITEM);
    }

    private void initBuyItem() {
        BUY_ITEM.onTransition((ctx, state) -> {
            // Maybe should I put this to cache since this list not changing?
            var items = apiClient.getAllItems(ctx.getGameId());
            var healPotion = PurchaseUtil.getHealPotion(ctx, items);
            healPotion.ifPresent(i -> {
                // Try to heal yourself as soon as possible
                if (ctx.getLives() < MAX_LIVES) {
                    apiClient.tryPurchaseItem(ctx.getGameId(), i.getId());
                }
            });
            var otherItem = PurchaseUtil.getAffordableItems(ctx, items);
            otherItem.ifPresent(i -> {
                // Prefer to have at least 50 gold to have a possibility to heal myself
                if (ctx.getGold() - i.getCost() >= HEAL_POTION_PRICE) {
                    apiClient.tryPurchaseItem(ctx.getGameId(), i.getId());
                }
            });
            // Increase expire count for existing quests in list
            ctx.setExpiresInCount(ctx.getExpiresInCount() + 1);
        }).target(TrainerEvent.INVESTIGATE, INVESTIGATE);
    }
}
