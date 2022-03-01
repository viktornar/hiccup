package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.ProbabilityType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestsUtil {
    protected static final Predicate<Quest> goodQuestPredicate = q -> {
        var goodTerms = List.of(
                "sell", "clean", "transport", "defend", "write", "agreement",
                "rescue", "create", "escort", "delivery", "intruders");
        var badTerms = List.of("steal", "kill");

        var anyGoodTerm = goodTerms.stream()
                .map(t -> q.getMessage().toLowerCase().contains(t))
                .filter(b -> b).findAny().orElse(false);

        var anyBadTerm = badTerms.stream()
                .map(t -> q.getMessage().toLowerCase().contains(t))
                .filter(b -> b).findAny().orElse(false);

        return anyGoodTerm && !anyBadTerm;
    };

    private static final List<ProbabilityType> SAFE_QUEST_LIST = List.of(
            ProbabilityType.PIECE_OF_CAKE,
            ProbabilityType.SURE_THING,
            ProbabilityType.QUITE_LIKELY,
            ProbabilityType.WALK_IN_PARK
    );

    private static final List<ProbabilityType> DANGEROUS_QUEST_LIST = List.of(
            ProbabilityType.RISKY,
            ProbabilityType.PLAYING_WITH_FIRE,
            ProbabilityType.RATHER_DETRIMENTAL
    );

    private static final List<ProbabilityType> GAMBLE_QUEST_LIST = List.of(
            ProbabilityType.GAMBLE,
            ProbabilityType.HMM
    );

    private static Optional<Quest> getQuest(TrainerContext ctx, List<ProbabilityType> probabilityTypes) {
        return ctx.getQuests().stream()
                .sorted(Comparator.comparingInt(Quest::getExpiresIn))
                .filter(q -> probabilityTypes.contains(ProbabilityType.of(q.getProbability())))
                .filter(goodQuestPredicate)
                .max(Comparator.comparingInt(Quest::getReward));
    }

    public static Optional<Quest> getSafeQuest(TrainerContext ctx) {
        return getQuest(ctx, SAFE_QUEST_LIST);
    }

    public static Optional<Quest> getDangerousQuest(TrainerContext ctx) {
        return getQuest(ctx, DANGEROUS_QUEST_LIST);
    }

    public static Optional<Quest> getGambleQuest(TrainerContext ctx) {
        return getQuest(ctx, GAMBLE_QUEST_LIST);
    }

    public static boolean checkIfNoMoreQuestsToSolve(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .allMatch(q -> ProbabilityType.IMPOSSIBLE.equals(ProbabilityType.of(q.getProbability())));
    }
}
