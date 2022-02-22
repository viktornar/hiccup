package com.github.viktornar.hiccup;

import com.github.viktornar.hiccup.game.character.OneLegTrainer;
import com.github.viktornar.hiccup.game.character.Trainer;
import com.github.viktornar.hiccup.game.character.TrainerContext;
import com.github.viktornar.hiccup.game.client.DragonOfMugloarClientV2;
import com.github.viktornar.hiccup.game.character.TrainerActions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class HiccupRunner implements CommandLineRunner {
    private final Trainer oneLegTrainer;

    @Override
    public void run(String... args) {
        log.info("Running application");
        oneLegTrainer.startAdventure();
    }
}
