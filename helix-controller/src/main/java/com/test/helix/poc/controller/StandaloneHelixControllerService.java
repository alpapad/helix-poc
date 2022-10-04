package com.test.helix.poc.controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.apache.helix.manager.zk.HelixManagerShutdownHook;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name="helix.controller.type", havingValue = "standalone", matchIfMissing = true)
public class StandaloneHelixControllerService implements ContollerService{

    private static Logger logger = LoggerFactory.getLogger(StandaloneHelixControllerService.class);

    
    private HelixManager manager = null;
    
    private final ControllerConfig controllerConfig;
    
    
    public StandaloneHelixControllerService(ControllerConfig controllerConfig) {
        super();
        this.controllerConfig = controllerConfig;
    }


    @Override
    @PostConstruct
    public void start() throws Exception {
        logger.info("Starting Standalone Helix controller");
        manager = HelixManagerFactory.getZKHelixManager(
                    controllerConfig.getClusterName(), 
                    controllerConfig.getInstanceName(), 
                    InstanceType.CONTROLLER, 
                    controllerConfig.getZookeeperAddress());
        
        manager.connect();
        
        logger.info("Helix Standalone controller started");
    }
    
    
    @Override
    @PreDestroy
    public void shutdown() {
        logger.info("Stopping Standalone Helix controller");
        if(manager != null) {
           try {
               manager.disconnect();
           } finally {
               manager = null;
           }
        }
    }
}
