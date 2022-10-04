package com.test.helix.poc.controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.model.LeaderStandbySMD;
import org.apache.helix.participant.DistClusterControllerStateModelFactory;
import org.apache.helix.participant.StateMachineEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.apache.helix.manager.zk.HelixManagerShutdownHook;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "helix.controller.type", havingValue = "distributed", matchIfMissing = false)
public class DistributedHelixControllerService implements ContollerService {

    private static Logger logger = LoggerFactory.getLogger(DistributedHelixControllerService.class);

    private HelixManager manager = null;

    private final ControllerConfig controllerConfig;

    public DistributedHelixControllerService(ControllerConfig controllerConfig) {
        super();
        this.controllerConfig = controllerConfig;
    }

    @Override
    @PostConstruct
    public void start() throws Exception {
        logger.info("Starting distributed Helix controller");

        manager = HelixManagerFactory.getZKHelixManager(controllerConfig.getClusterName(),
                controllerConfig.getInstanceName(), InstanceType.CONTROLLER_PARTICIPANT,
                controllerConfig.getZookeeperAddress());

        DistClusterControllerStateModelFactory stateModelFactory = new DistClusterControllerStateModelFactory(
                controllerConfig.getZookeeperAddress());

        StateMachineEngine stateMach = manager.getStateMachineEngine();
        stateMach.registerStateModelFactory(LeaderStandbySMD.name, stateModelFactory);

        manager.connect();

        logger.info("Helix distributed controller started");
    }

    @Override
    @PreDestroy
    public void shutdown() {
        logger.info("Stopping distributed Helix controller");
        if (manager != null) {
            try {
                manager.disconnect();
            } finally {
                manager = null;
            }
        }
    }
}
