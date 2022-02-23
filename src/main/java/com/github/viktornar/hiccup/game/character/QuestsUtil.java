package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.ProbabilityType;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class QuestsUtil {
    private static final Predicate<Quest> goodQuestPredicate = q ->
            q.getMessage().contains("Rescue") ||
                    q.getMessage().contains("Create") ||
                    q.getMessage().contains("Escort") ||
                    q.getMessage().contains("Help defending");

    private QuestsUtil() {
    }

    public static Optional<Quest> getSafeQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q ->
                        ProbabilityType.PIECE_OF_CAKE.equals(ProbabilityType.of(q.getProbability())) ||
                                ProbabilityType.SURE_THING.equals(ProbabilityType.of(q.getProbability())) ||
                                ProbabilityType.QUITE_LIKELY.equals(ProbabilityType.of(q.getProbability())) ||
                                ProbabilityType.WALK_IN_PARK.equals(ProbabilityType.of(q.getProbability())))
                .filter(goodQuestPredicate)
                .max(Comparator.comparingInt(Quest::getReward));
    }

    public static Optional<Quest> getDangerousQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q -> ProbabilityType.RISKY.equals(ProbabilityType.of(q.getProbability())) ||
                        ProbabilityType.PLAYING_WITH_FIRE.equals(ProbabilityType.of(q.getProbability())))
                .filter(goodQuestPredicate)
                .max(Comparator.comparingInt(Quest::getReward));
    }

    public static Optional<Quest> getGambleQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q -> ProbabilityType.GAMBLE.equals(ProbabilityType.of(q.getProbability())) ||
                        ProbabilityType.HMM.equals(ProbabilityType.of(q.getProbability())))
                .filter(goodQuestPredicate)
                .max(Comparator.comparingInt(Quest::getReward));
    }

    public static boolean checkIfNoMoreQuestsToSolve(TrainerContext ctx) {
        return ctx.getQuests().stream().allMatch(q -> ProbabilityType.IMPOSSIBLE.equals(ProbabilityType.of(q.getProbability())));
    }
}
