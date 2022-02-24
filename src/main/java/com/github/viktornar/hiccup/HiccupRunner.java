package com.github.viktornar.hiccup;

import com.github.viktornar.hiccup.game.character.OneLegTrainer;
import com.github.viktornar.hiccup.game.character.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
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
@SuppressWarnings({"java:S106"})
public class HiccupRunner implements CommandLineRunner {
    public static final String MAX_TURN_OPTION = "maxTurn";
    public static final String HELP_OPTION = "help";
    public static final String APP_NAME = "hiccup";
    private final Trainer oneLegTrainer;

    @Override
    public void run(String... args) {
        log.info("Running application");
        Options options = new Options();
        options.addOption(HELP_OPTION, false, "print this help");
        options.addOption(MAX_TURN_OPTION, true, "max turn number");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        var maxTurnNumber = OneLegTrainer.MAX_TURN;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption(HELP_OPTION)) {
                formatter.printHelp(APP_NAME, options);
                return;
            }

            if (cmd.hasOption(MAX_TURN_OPTION)) {
                maxTurnNumber = Integer.parseInt(cmd.getOptionValue(MAX_TURN_OPTION));
            }

            oneLegTrainer.startAdventure(maxTurnNumber);
            var trainerContext = oneLegTrainer.getContext();
            log.debug("Final state of trainer after finish dragon train adventure: {}", trainerContext);

            System.out.println("-----------------------------------------------");
            if (trainerContext.getLives() == null || trainerContext.getLives() == 0) {
                System.out.printf("Dragon trainer is dead.  He/she was at %s level with score %s%n",
                        trainerContext.getLevel(),
                        trainerContext.getScore());
            } else {
                System.out.printf("Dragon trainer has finished training.  He/she have %s level with score %s. " +
                                "Could not reach anything more.%n",
                        trainerContext.getLevel(),
                        trainerContext.getScore());
            }
            System.out.println("-----------------------------------------------");
        } catch (ParseException e) {
            log.error("{}", e.getMessage());
            formatter.printHelp(APP_NAME, options);
        }
    }
}
