package com.test.helix.poc.participant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(ParticipantConfig.class)
public class ParticipantAplication {

    public static void main(String[] args) {
        SpringApplication.run(ParticipantAplication.class, args);
    }
}
