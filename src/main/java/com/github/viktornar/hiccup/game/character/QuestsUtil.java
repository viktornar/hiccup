package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.Probability;

import java.util.Comparator;
import java.util.Optional;

public class QuestsUtil {
    private QuestsUtil() {
    }

    public static Optional<Quest> getSafeQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q ->
                        Probability.PIECE_OF_CAKE.equals(Probability.of(q.getProbability())) ||
                                Probability.SURE_THING.equals(Probability.of(q.getProbability())) ||
                                Probability.QUITE_LIKELY.equals(Probability.of(q.getProbability())) ||
                                Probability.WALK_IN_PARK.equals(Probability.of(q.getProbability())))
                .max(Comparator.comparingInt(Quest::getReward));
    }
}
