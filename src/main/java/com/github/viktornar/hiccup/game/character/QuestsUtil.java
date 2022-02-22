package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.Probability;

import java.util.Optional;

public class QuestsUtil {
    private QuestsUtil() {
    }

    public static Optional<Quest> getEasiestQuest(TrainerContext ctx) {
        return ctx.getQuests().stream()
                .filter(q -> ctx.getExpiresInCount() < q.getExpiresIn())
                .filter(q ->
                        Probability.VERY_EASY.equals(Probability.of(q.getProbability())) ||
                                Probability.EASY.equals(Probability.of(q.getProbability()))
                )
                .findFirst();
    }
}
