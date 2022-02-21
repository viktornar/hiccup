package com.github.viktornar.hiccup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "hiccup.gameService.baseUrlForAPIv2: https://foo.bar"
)
@ActiveProfiles("test")
class HiccupPropertiesTest {
    @Autowired
    private HiccupProperties properties;

    @Test
    void should_get_correct_base_api_for_url_without_throwing_exception() {
        assertDoesNotThrow(() -> properties.getGameService());
        assertEquals("https://foo.bar", properties.getGameService().getBaseUrlForAPIv2());
    }
}