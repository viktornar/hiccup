package com.github.viktornar.hiccup.game.client;

import com.github.viktornar.hiccup.HiccupProperties;
import com.github.viktornar.hiccup.game.dto.*;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class DragonOfMugloarClientV2 implements BaseAPIV2Client {
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public DragonOfMugloarClientV2(HiccupProperties properties, RestTemplateBuilder restTemplateBuilder) {
        log.info("Initializing client with base url {}", properties.getGameService().getBaseUrlForAPIv2());
        baseUrl = properties.getGameService().getBaseUrlForAPIv2();
        restTemplate = restTemplateBuilder.rootUri(baseUrl).build();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public Game startGame() {
        var url = String.format("%s" + START_GAME_PATH, baseUrl);
        return restTemplate.postForObject(url, null, Game.class);
    }

    @Override
    public Reputation investigateReputation(String gameId) {
        var url = String.format("%s" + INVESTIGATION_PATH, baseUrl, gameId);
        return restTemplate.postForObject(url, null, Reputation.class);
    }

    @Override
    public List<Quest> getAllQuests(String gameId) {
        var url = String.format("%s" + ALL_QUEST_PATH, baseUrl, gameId);
        var quests = restTemplate.getForObject(url, Quest[].class);
        if (quests == null) {
            return Collections.emptyList();
        }
        return List.of(quests);
    }

    @Override
    public Reward trySolveQuest(String gameId, String adId) {
        var url = String.format("%s" + SOLVE_QUEST_PATH, baseUrl, gameId, adId);
        return restTemplate.postForObject(url, null, Reward.class);
    }

    @Override
    public List<Item> getAllItems(String gameId) {
        var url = String.format("%s" + ITEMS_LIST_IN_SHOP_PATH, baseUrl, gameId);
        var items = restTemplate.getForObject(url, Item[].class);
        if (items == null) {
            return Collections.emptyList();
        }
        return List.of(items);
    }

    @Override
    public Basket tryPurchaseItem(String gameId, String adId) {
        var url = String.format("%s" + BUY_ITEM_IN_SHOP_PATH, baseUrl, gameId, adId);
        return restTemplate.postForObject(url, null, Basket.class);
    }
}
