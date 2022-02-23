package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.Probability;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestsUtilTest {

    @Test
    void should_get_easiest_quests() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Walk in the park");
                setExpiresIn(4);
            }});
            add(new Quest() {{
                setProbability("Piece of cake");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(2);
            }});
        }};

        context.setExpiresInCount(2);
        context.setQuests(quests);

        var quest = QuestsUtil.getEasiestQuest(context);

        assertTrue(quest.isPresent());

        quest.ifPresent(q -> {
            assertTrue(Probability.EASY.equals(Probability.of(q.getProbability())) ||
                  Probability.VERY_EASY.equals(Probability.of(q.getProbability())));

            assertNotEquals(Probability.HARD, Probability.of(q.getProbability()));
        });
    }

    @Test
    void should_not_get_easiest_quests_when_only_hard() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Walk in the park");
                setExpiresIn(4);
            }});
        }};

        context.setExpiresInCount(2);
        context.setQuests(quests);

        var quest = QuestsUtil.getEasiestQuest(context);

        assertFalse(quest.isPresent());
    }

    @Test
    void should_not_get_if_expired() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(2);
            }});
        }};

        context.setExpiresInCount(3); // or 2 as well
        context.setQuests(quests);

        var quest = QuestsUtil.getEasiestQuest(context);

        assertFalse(quest.isPresent());
    }
}