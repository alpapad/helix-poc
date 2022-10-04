package com.test.helix.poc.spectator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SpectatorConfig.class)
public class Spectator {

    public static void main(String[] args) {
        SpringApplication.run(Spectator.class, args);
    }
}
