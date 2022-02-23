package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.ProbabilityType;

import java.util.Comparator;
import java.util.Optional;

public class QuestsUtil {
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
                .max(Comparator.comparingInt(Quest::getReward));
    }

    public static Optional<Quest> getImpossibleQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q -> ProbabilityType.IMPOSSIBLE.equals(ProbabilityType.of(q.getProbability())))
                .max(Comparator.comparingInt(Quest::getReward));
    }
}
