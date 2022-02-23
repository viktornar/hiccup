package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.ProbabilityType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class QuestsUtilTest {

    @Test
    void should_get_easiest_quests() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Risky");
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

        var quest = QuestsUtil.getSafeQuest(context);

        assertTrue(quest.isPresent());

        quest.ifPresent(q -> {
            assertTrue(ProbabilityType.SURE_THING.equals(ProbabilityType.of(q.getProbability())) ||
                  ProbabilityType.PIECE_OF_CAKE.equals(ProbabilityType.of(q.getProbability())));

            assertNotEquals(ProbabilityType.IMPOSSIBLE, ProbabilityType.of(q.getProbability()));
        });
    }

    @Test
    void should_not_get_easiest_quests_when_only_hard() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Risky");
                setExpiresIn(4);
            }});
        }};

        context.setExpiresInCount(2);
        context.setQuests(quests);

        var quest = QuestsUtil.getSafeQuest(context);

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

        var quest = QuestsUtil.getSafeQuest(context);

        assertFalse(quest.isPresent());
    }

    @Test
    void should_get_quest_with_max_reward() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(4);
                setReward(12);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(4);
                setReward(23);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(4);
                setReward(2);
            }});
        }};

        context.setExpiresInCount(2);
        context.setQuests(quests);

        var quest = QuestsUtil.getSafeQuest(context);

        assertTrue(quest.isPresent());
        quest.ifPresent(q -> assertEquals(23, q.getReward()));
    }
}