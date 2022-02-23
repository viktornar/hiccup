package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Item;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseUtilTest {
    @Test
    void should_get_heal_potion() {
        List<Item> items = new ArrayList<>() {{
           add(new Item() {{
               setId("hpot");
           }});
            add(new Item() {{
                setId("wc");
            }});
        }};

        var ctx = new TrainerContext() {{
           setGold(60);
        }};

        var item = PurchaseUtil.getHealPotion(ctx, items);

        assertTrue(item.isPresent());
        assertEquals("hpot", item.get().getId());
    }

    @Test
    void should_get_affordable_items() {
        List<Item> items = new ArrayList<>() {{
            add(new Item() {{
                setId("hpot");
                setCost(50);
            }});
            add(new Item() {{
                setId("wc");
                setCost(60);
            }});
            add(new Item() {{
                setId("as");
                setCost(190);
            }});
        }};

        var ctx = new TrainerContext() {{
            setGold(60);
        }};

        var item = PurchaseUtil.getAffordableItems(ctx, items);

        assertTrue(item.isPresent());
        assertEquals("wc", item.get().getId());
    }
}