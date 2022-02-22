package com.github.viktornar.hiccup;

import com.github.viktornar.hiccup.game.character.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "hiccup.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class HiccupRunner implements CommandLineRunner {
    private final Trainer oneLegTrainer;

    @Override
    public void run(String... args) {
        log.info("Running application");
        oneLegTrainer.startAdventure();
        var trainerContext = oneLegTrainer.getContext();
        log.info("Final state of trainer after finish dragon train adventure: {}", trainerContext);
    }
}
