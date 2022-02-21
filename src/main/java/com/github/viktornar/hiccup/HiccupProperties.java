package com.github.viktornar.hiccup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("hiccup")
@Validated
public class HiccupProperties {
    @Getter
    private final GameService gameService = new GameService();

    @Getter
    @Setter
    public static class GameService {
        // Default
        private String baseUrlForAPIv2 = "https://dragonsofmugloar.com";
    }
}
