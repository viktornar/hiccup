package com.github.viktornar.hiccup.dragon.client;

import com.github.viktornar.hiccup.HiccupProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DragonOfMugloarClientV2Test {
    private DragonOfMugloarClientV2 dragonOfMugloarClientV2;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        HiccupProperties properties = mock(HiccupProperties.class);
        HiccupProperties.GameService gameService = new HiccupProperties.GameService();
        gameService.setBaseUrlForAPIv2("https://dragonsofmugloar.com");
        when(properties.getGameService()).thenReturn(gameService);
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.rootUri(any(String.class))).thenReturn(restTemplateBuilder);
        restTemplate = mock(RestTemplate.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        dragonOfMugloarClientV2 = new DragonOfMugloarClientV2(properties, restTemplateBuilder);
    }

    @Test
    void should_have_injected_properties_that_set_correct_base_api_url() {
        assertEquals("https://dragonsofmugloar.com", dragonOfMugloarClientV2.getBaseUrl());
    }
}