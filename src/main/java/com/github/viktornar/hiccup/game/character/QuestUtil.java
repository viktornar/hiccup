package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.Probability;

import java.util.List;
import java.util.Optional;

public class QuestUtil {
    private QuestUtil() {
    }

    public static Optional<Quest> getEasiestQuest(List<Quest> questList) {
        return questList.stream()
                .filter(q -> Probability.of(q.getProbability()).equals(Probability.VERY_EASY))
                .findFirst();
    }
}
