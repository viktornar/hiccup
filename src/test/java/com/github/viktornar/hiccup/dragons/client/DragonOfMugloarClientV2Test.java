package com.github.viktornar.hiccup.dragons.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DragonOfMugloarClientV2Test {
    @Autowired
    private DragonOfMugloarClientV2 dragonOfMugloarClientV2;

    @Test
    void should_have_injected_properties_that_set_correct_base_api_url() {
        assertEquals("https://dragonsofmugloar.com/api/v2", dragonOfMugloarClientV2.getBaseUrl());
    }
}