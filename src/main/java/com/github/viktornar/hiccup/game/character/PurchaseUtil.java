package com.github.viktornar.hiccup.game.character;

import com.github.viktornar.hiccup.game.data.Item;
import com.github.viktornar.hiccup.game.type.ItemType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PurchaseUtil {
    public static Optional<Item> getAffordableItems(TrainerContext ctx, List<Item> items) {
        return items.stream()
                .filter(i -> !ctx.getPurchasedItems().contains(i))
                .filter(i -> !ItemType.HEALING_POTION.getId().equals(i.getId()))
                .filter(i -> ctx.getGold() >= i.getCost())
                .findFirst();
    }

    public static Optional<Item> getHealPotion(TrainerContext ctx, List<Item> items) {
        return items.stream()
                .filter(i -> ItemType.HEALING_POTION.getId().equals(i.getId()))
                .filter(i -> ctx.getGold() >= i.getCost())
                .findFirst();
    }
}
