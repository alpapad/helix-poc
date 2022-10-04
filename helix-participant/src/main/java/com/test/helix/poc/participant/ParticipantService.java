package com.test.helix.poc.participant;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.participant.StateMachineEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.test.helix.poc.statemodel.LeaderFollowerSMD;

@Service
public class ParticipantService {
    private final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

    private HelixManager manager = null;
    private final ParticipantConfig config;

    public ParticipantService(ParticipantConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void start() throws Exception {
        logger.info("Starting Helix cluster participant (node)");
        manager = HelixManagerFactory.getZKHelixManager(config.getClusterName(), config.getInstanceName(),
                InstanceType.PARTICIPANT, config.getZookeeperAddress());

        StateMachineEngine stateMach = manager.getStateMachineEngine();
        ClusterStateModelFactory msModelFactory = new ClusterStateModelFactory();
        stateMach.registerStateModelFactory(LeaderFollowerSMD.name, msModelFactory);

        manager.connect();
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down Helix participant");
        if (manager != null) {
            try {
                manager.disconnect();
            } finally {
                manager = null;
            }
        }
    }
}
