package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Quest;
import com.github.viktornar.hiccup.game.type.ProbabilityType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class QuestsUtilTest {

    @Test
    void should_get_easiest_quests() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Risky");
                setMessage("help");
                setExpiresIn(4);
            }});
            add(new Quest() {{
                setProbability("Piece of cake");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(2);
            }});
        }};

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
    void should_get_dangerous_quests() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Risky");
                setMessage("help");
                setExpiresIn(4);
            }});
            add(new Quest() {{
                setProbability("Piece of cake");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(2);
            }});
        }};

        context.setQuests(quests);

        var quest = QuestsUtil.getDangerousQuest(context);

        assertTrue(quest.isPresent());

        quest.ifPresent(q -> {
            assertFalse(ProbabilityType.SURE_THING.equals(ProbabilityType.of(q.getProbability())) ||
                    ProbabilityType.PIECE_OF_CAKE.equals(ProbabilityType.of(q.getProbability())));

            assertEquals(ProbabilityType.RISKY, ProbabilityType.of(q.getProbability()));
        });
    }

    @Test
    void should_get_gamble_quests() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Gamble");
                setMessage("help");
                setExpiresIn(4);
            }});
            add(new Quest() {{
                setProbability("Piece of cake");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(3);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(2);
            }});
        }};

        context.setQuests(quests);

        var quest = QuestsUtil.getGambleQuest(context);

        assertTrue(quest.isPresent());

        quest.ifPresent(q -> {
            assertFalse(ProbabilityType.SURE_THING.equals(ProbabilityType.of(q.getProbability())) ||
                    ProbabilityType.PIECE_OF_CAKE.equals(ProbabilityType.of(q.getProbability())));

            assertNotEquals(ProbabilityType.RISKY, ProbabilityType.of(q.getProbability()));
            assertEquals(ProbabilityType.GAMBLE, ProbabilityType.of(q.getProbability()));
        });
    }

    @Test
    void should_not_get_easiest_quests_when_only_hard() {
        var context = new TrainerContext();
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Risky");
                setMessage("help");
                setExpiresIn(4);
            }});
        }};

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
                setMessage("help");
                setReward(12);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(4);
                setReward(23);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help");
                setExpiresIn(4);
                setReward(2);
            }});
        }};

        context.setQuests(quests);

        var quest = QuestsUtil.getSafeQuest(context);
        assertTrue(quest.isPresent());
        quest.ifPresent(q -> assertEquals(23, q.getReward()));
    }

    @Test
    void should_filter_correct_quests_by_message() {
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Sure thing");
                setExpiresIn(4);
                setMessage("create");
                setReward(12);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help steal");
                setExpiresIn(4);
                setReward(23);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help kill");
                setExpiresIn(4);
                setReward(2);
            }});
        }};

        var goodQuests = quests.stream().filter(QuestsUtil.goodQuestPredicate).toList();

        assertEquals(1, goodQuests.size());
        assertTrue(goodQuests.get(0).getMessage().contains("create"));
    }

    @Test
    void should_check_if_we_any_possible_to_solve_quest_exist() {
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Impossible");
                setExpiresIn(4);
                setMessage("create");
                setReward(12);
            }});
            add(new Quest() {{
                setProbability("Impossible");
                setMessage("help steal");
                setExpiresIn(4);
                setReward(23);
            }});
            add(new Quest() {{
                setProbability("Sure thing");
                setMessage("help kill");
                setExpiresIn(4);
                setReward(2);
            }});
        }};

        var ctx = new TrainerContext() {{
           getQuests().addAll(quests);
        }};

        assertFalse(QuestsUtil.checkIfNoMoreQuestsToSolve(ctx));
    }

    @Test
    void should_check_that_no_more_quests_exist() {
        var quests = new ArrayList<Quest>() {{
            add(new Quest() {{
                setProbability("Impossible");
                setExpiresIn(4);
                setMessage("create");
                setReward(12);
            }});
            add(new Quest() {{
                setProbability("Impossible");
                setMessage("help steal");
                setExpiresIn(4);
                setReward(23);
            }});
        }};

        var ctx = new TrainerContext() {{
            getQuests().addAll(quests);
        }};

        assertTrue(QuestsUtil.checkIfNoMoreQuestsToSolve(ctx));
    }
}