package com.test.helix.poc.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClusterSetupConfig.class)
public class ClusterSetupApplication implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(ClusterSetupApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ClusterSetupApplication.class, args);
    }

    @Autowired
    private ClusterSetupService cfgService;;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Helix cluster setup");
        cfgService.setup();
        logger.info("Helix cluster setup completed");
    }

}
