package com.github.viktornar.hiccup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "hiccup.runner.enabled=false"
        }
)
class HiccupApplicationTests {

    @Test
    void contextLoads() {
    }

}
