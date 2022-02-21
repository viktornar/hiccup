package com.github.viktornar.hiccup.dragon.client;

import com.github.viktornar.hiccup.HiccupProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DragonOfMugloarClientV2 implements BaseAPIV2Client {
    private final String baseUrl;

    public DragonOfMugloarClientV2(HiccupProperties properties, RestTemplateBuilder restTemplateBuilder) {
        log.info("Initializing client with base url {}", properties.getGameService().getBaseUrlForAPIv2());
        baseUrl = properties.getGameService().getBaseUrlForAPIv2();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
