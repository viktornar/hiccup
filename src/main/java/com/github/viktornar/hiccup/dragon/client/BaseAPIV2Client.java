package com.github.viktornar.hiccup.dragon.client;

import com.github.viktornar.hiccup.dragon.dto.*;

import java.util.List;

public interface BaseAPIV2Client {
    String START_GAME_PATH = "/api/v2/game/start";
    String INVESTIGATION_PATH = "/api/v2/%s/investigate/reputation";
    String ALL_QUEST_PATH = "/api/v2/%s/messages";
    String SOLVE_QUEST_PATH = "/api/v2/%s/solve/%s";
    String ITEMS_LIST_IN_SHOP_PATH = "/api/v2/%s/shop";
    String BUY_ITEM_IN_SHOP_PATH = "/api/v2/%s/shop/buy/%s";

    String getBaseUrl();

    Game startGame();

    Reputation investigateReputation(String gameId);

    List<Quest> getAllQuest(String gameId);

    Reward trySolveQuest(String gameId, String adId);

    List<Item> getAllItems(String gameId);

    Basket tryPurchaseItem(String gameId, String adId);
}
