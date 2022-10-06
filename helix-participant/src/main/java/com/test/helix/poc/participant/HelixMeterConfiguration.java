package com.test.helix.poc.participant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.test.helix.poc.participant.metrics.HelixMeterBinder;

@Configuration
@Order
public class HelixMeterConfiguration {

    @Bean
    HelixMeterBinder helixMeterBinder(ParticipantConfig config) {
        return new HelixMeterBinder(config.getClusterName(), config.getInstanceName());
    }
}
