package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.Probability;

import java.util.Optional;

public class QuestUtil {
    private QuestUtil() {
    }

    public static Optional<Quest> getEasiestQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q ->
                        Probability.of(q.getProbability()).equals(Probability.VERY_EASY) &&
                                ctx.getExpiresInCount() < q.getExpiresIn()
                )
                .findFirst();
    }
}
