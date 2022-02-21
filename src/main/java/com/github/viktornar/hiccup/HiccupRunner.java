package com.github.viktornar.hiccup;

import com.github.viktornar.hiccup.game.client.DragonOfMugloarClientV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HiccupRunner implements CommandLineRunner {
    private final DragonOfMugloarClientV2 dragonOfMugloarClientV2;

    @Override
    public void run(String... args) {
        log.info("Running application");
        var game = dragonOfMugloarClientV2.startGame();
        var gameId = game.getGameId();
        var reputation = dragonOfMugloarClientV2.investigateReputation(gameId);
        log.info("Reputation {{}} for game {{}}", reputation, game);
    }
}
