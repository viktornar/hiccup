package com.github.viktornar.hiccup.game.client;

import com.github.viktornar.hiccup.game.dto.*;

import java.util.List;

public interface APIClient {
    String getBaseUrl();

    Game startGame();

    Reputation investigateReputation(String gameId);

    List<Quest> getAllQuests(String gameId);

    Reward trySolveQuest(String gameId, String adId);

    List<Item> getAllItems(String gameId);

    Basket tryPurchaseItem(String gameId, String adId);
}
