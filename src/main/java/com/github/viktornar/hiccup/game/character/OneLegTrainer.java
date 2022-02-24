package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.client.APIClient;
import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.data.Reward;
import com.github.viktornar.hiccup.game.mapper.DataToTrainerContextMapper;
import io.reactivex.rxjava3.functions.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class OneLegTrainer implements Trainer {
    public static final int MAX_TURN = 150;
    public static final int HEAL_POTION_PRICE = 50;
    public static final int MAX_LIVES = 3;
    public static final int NO_LIVES = 0;
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
    private static final TrainerActions.State<TrainerContext, TrainerEvent> SOLVE_DANGEROUS_QUESTS =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> SOLVE_GAMBLE_QUESTS =
            new TrainerActions.State<>();
    private static final TrainerActions.State<TrainerContext, TrainerEvent> BUY_ITEM =
            new TrainerActions.State<>();
    private final APIClient apiClient;
    private final TrainerContext context;
    private final TrainerActions<TrainerContext, TrainerEvent> oneLegTrainerActions;

    private int maxTurn = MAX_TURN;

    Predicate<TrainerContext> shouldEndFlow = ctx ->
            (ctx.getTurn() != null && ctx.getTurn() >= this.maxTurn) ||
                    (ctx.getLives() != null && ctx.getLives() == NO_LIVES) || ctx.isGameOver();

    public OneLegTrainer(APIClient apiClient) {
        this.apiClient = apiClient;
        context = new TrainerContext();
        oneLegTrainerActions = new TrainerActions<>(context, IDLE);
        // Listen to actions
        oneLegTrainerActions.connect().subscribe();
    }

    @Override
    public void startAdventure(int maxTurn) {
        this.maxTurn = Math.min(maxTurn, MAX_TURN);
        // Register flows that can have tangled connection between each other :D
        IDLE.target(TrainerEvent.START, START);
        initTraining();
        initGame();
        initQuests();
        initInvestigate();
        initSafeQuestsSolver();
        initGambleQuestsSolver();
        initDangerousQuestsSolver();
        initBuyItem();
        // Start the game
        log.info("Starting train a dragon");
        oneLegTrainerActions.accept(TrainerEvent.START);
        while (!IDLE.equals(oneLegTrainerActions.getState()) &&
                !context.isGameOver()) {
            // Adventurer is busy by doing dragon training :)
            oneLegTrainerActions.accept(TrainerEvent.REGISTER);
            if (context.isGameOver()) {
                log.info("Trainer are dead. You lost your game");
            }
        }
    }

    @Override
    public TrainerContext getContext() {
        return context;
    }

    protected void initTraining() {
        START.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.START.name(), ctx);

            if (shouldEndFlow.test(ctx)) {
                log.info("Max turns limit reached or trainer was killed. Terminating adventure.");
                START.target(TrainerEvent.IDLE, IDLE);
                oneLegTrainerActions.accept(TrainerEvent.IDLE);
            }
        }).target(TrainerEvent.REGISTER, REGISTER);
    }

    protected void initGame() {
        REGISTER.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.REGISTER.name(), ctx);
            if (!StringUtils.hasText(ctx.getGameId())) {
                var game = apiClient.startGame();
                var newCtx = DataToTrainerContextMapper.INSTANCE.gameToContext(game);
                // Initial state. We need to init first step as 0
                context.setTurn(0);
                ctx.from(newCtx);
                log.info("Registering a new game with id: {}", game.getGameId());
            }
            // I'm brave trainer. Let's start solving quests.
            oneLegTrainerActions.accept(TrainerEvent.GET_QUESTS);
        }).target(TrainerEvent.GET_QUESTS, GET_QUESTS);
    }

    private void initInvestigate() {
        INVESTIGATE.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.INVESTIGATE.name(), ctx);
            var reputation = apiClient.investigateReputation(ctx.getGameId());
            var newCtx = DataToTrainerContextMapper.INSTANCE.reputationToContext(reputation);
            ctx.from(newCtx);
            log.info("Trainer level: {{}}, score: {{}}, lives: {{}}, people: {{}}, state: {{}}, underworld: {{}} " +
                            "- after turn {{}}",
                    ctx.getLevel(),
                    ctx.getScore(),
                    ctx.getLives(),
                    ctx.getPeople(),
                    ctx.getState(),
                    ctx.getUnderworld(),
                    ctx.getTurn()
            );
            // Start over again
            oneLegTrainerActions.accept(TrainerEvent.START);
        }).target(TrainerEvent.START, START);
    }

    private void initQuests() {
        GET_QUESTS.onTransition((ctx, state) -> {
            log.info("Receiving quests");
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.GET_QUESTS.name(), ctx);
            var quests = apiClient.getAllQuests(ctx.getGameId());
            log.debug("Received quests to solve:");
            quests.forEach(q -> log.debug("\tquest: {}", q));
            ctx.setQuests(quests);
            // Try to solve only easy quests
            oneLegTrainerActions.accept(TrainerEvent.SOLVE_SAFE_QUESTS);
        }).target(TrainerEvent.SOLVE_SAFE_QUESTS, SOLVE_SAFE_QUESTS);
    }

    private void initSafeQuestsSolver() {
        SOLVE_SAFE_QUESTS.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.SOLVE_SAFE_QUESTS.name(), ctx);
            var quest = QuestsUtil.getSafeQuest(ctx);
            if (quest.isPresent()) {
                log.info("Will try to solve simple quest: {}", quest.get());
                tryToSolveAndUpdateCtx(ctx, quest.get());
                oneLegTrainerActions.accept(TrainerEvent.BUY_ITEM);
            } else {
                log.info("No simple quest to solve. Going to try my luck.");
                SOLVE_SAFE_QUESTS.target(TrainerEvent.SOLVE_GAMBLE_QUESTS, SOLVE_GAMBLE_QUESTS);
                oneLegTrainerActions.accept(TrainerEvent.SOLVE_GAMBLE_QUESTS);
            }
        }).target(TrainerEvent.BUY_ITEM, BUY_ITEM);
    }

    private void initGambleQuestsSolver() {
        SOLVE_GAMBLE_QUESTS.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.SOLVE_GAMBLE_QUESTS.name(), ctx);
            var quest = QuestsUtil.getGambleQuest(ctx);
            if (quest.isPresent()) {
                log.info("Will try to solve quest: {}", quest.get());
                tryToSolveAndUpdateCtx(ctx, quest.get());
                oneLegTrainerActions.accept(TrainerEvent.BUY_ITEM);
            } else {
                log.info("Seems that need to start a real man work.");
                SOLVE_GAMBLE_QUESTS.target(TrainerEvent.SOLVE_DANGEROUS_QUESTS, SOLVE_DANGEROUS_QUESTS);
                oneLegTrainerActions.accept(TrainerEvent.SOLVE_DANGEROUS_QUESTS);
            }
        }).target(TrainerEvent.BUY_ITEM, BUY_ITEM);
    }

    private void initDangerousQuestsSolver() {
        SOLVE_DANGEROUS_QUESTS.onTransition((ctx, state) -> {
            log.debug(STEP_CONTEXT_LOG_TEXT, TrainerEvent.SOLVE_DANGEROUS_QUESTS.name(), ctx);
            var quest = QuestsUtil.getDangerousQuest(ctx);
            if (quest.isPresent()) {
                log.debug("Will try to solve dangerous quest: {}", quest.get());
                tryToSolveAndUpdateCtx(ctx, quest.get());
            }

            if (QuestsUtil.checkIfNoMoreQuestsToSolve(ctx)) {
                ctx.setGameOver(true);
                log.info("No more quests possible to solve.");
            }
            // After quest trainer can be injured, so he need to heal him before finishing training
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
                    log.info("Will try to heal myself.");
                    var basket = apiClient.tryPurchaseItem(ctx.getGameId(), i.getId());
                    var newCtx = DataToTrainerContextMapper.INSTANCE.basketToContext(basket);
                    ctx.from(newCtx);
                }
            });
            var otherItem = PurchaseUtil.getAffordableItems(ctx, items);
            otherItem.ifPresent(i -> {
                // Prefer to have at least 50 gold to have a possibility to heal myself
                if (ctx.getGold() - i.getCost() >= HEAL_POTION_PRICE) {
                    log.info("Will try to buy some nice stuff for myself.");
                    var basket = apiClient.tryPurchaseItem(ctx.getGameId(), i.getId());
                    if (basket.isShoppingSuccess()) {
                        log.info("Bought some item: {{}}", i);
                        ctx.getPurchasedItems().add(i);
                    }
                    var newCtx = DataToTrainerContextMapper.INSTANCE.basketToContext(basket);
                    ctx.from(newCtx);
                }
            });
            oneLegTrainerActions.accept(TrainerEvent.INVESTIGATE);
        }).target(TrainerEvent.INVESTIGATE, INVESTIGATE);
    }

    private void tryToSolveAndUpdateCtx(TrainerContext ctx, Quest quest) {
        Reward reward = apiClient.trySolveQuest(ctx.getGameId(), quest.getAdId());
        log.info("Reward (or not) from quest: {}", reward);
        var newQuests = ctx.getQuests();
        // Remove quest. It doesn't exist anymore for trainer
        newQuests.remove(quest);
        var newCtx = DataToTrainerContextMapper.INSTANCE.rewardToContext(reward);
        newCtx.getQuests().addAll(newQuests);
        ctx.from(newCtx);
    }
}
