package com.github.viktornar.hiccup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HiccupProperties.class)
@Slf4j
public class HiccupApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(HiccupApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Running application");
    }
}
