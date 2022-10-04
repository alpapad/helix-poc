package com.test.helix.poc.setup;

import java.util.HashMap;
import java.util.Map;

import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.manager.zk.ZKHelixManager;
import org.apache.helix.model.ClusterConfig.ClusterConfigProperty;
import org.apache.helix.model.HelixConfigScope;
import org.apache.helix.model.HelixConfigScope.ConfigScopeProperty;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.ResourceConfig;
import org.apache.helix.model.StateModelDefinition;
import org.apache.helix.model.builder.FullAutoModeISBuilder;
import org.apache.helix.model.builder.HelixConfigScopeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.test.helix.poc.statemodel.LeaderFollowerSMD;

@Service
public class ClusterSetupService {
    private static Logger logger = LoggerFactory.getLogger(ClusterSetupService.class);

    private final ClusterSetupConfig config;


    public ClusterSetupService(ClusterSetupConfig config) {
        super();
        this.config = config;
    }

    public void setup() {
        ZKHelixAdmin admin = new ZKHelixAdmin.Builder().setZkAddress(config.getZookeeperAddress()).build();
        try {
            logger.info("Creating cluster {}", config.getClusterName());
            final String clusterName = config.getClusterName();

            if (config.isDrop()) {
                logger.info("dropping existing cluster {}", config.getClusterName());
                try {
                    admin.dropCluster(clusterName);
                } catch (Exception e) {
                    logger.error("Could not drop cluster {}", clusterName, e);
                }
            }

            logger.info("recreating cluster {}", config.getClusterName());
            admin.addCluster(config.getClusterName(), config.isRecreateIfExists());

            HelixConfigScope scope = new HelixConfigScopeBuilder(ConfigScopeProperty.CLUSTER).forCluster(clusterName)
                    .build();

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ZKHelixManager.ALLOW_PARTICIPANT_AUTO_JOIN, "true");
            properties.put(ClusterConfigProperty.PERSIST_INTERMEDIATE_ASSIGNMENT.name(), "true");
            admin.setConfig(scope, properties);

            logger.info("Setting cluster {} state machine", config.getClusterName());
            // set state machine to be used
            admin.addStateModelDef(clusterName, LeaderFollowerSMD.name,
                    new StateModelDefinition(LeaderFollowerSMD.build().getRecord()));

            logger.info("Adding participants to cluster {}", config.getClusterName());
            config.getTopology().forEach(participant -> {
                final InstanceConfig instance = new InstanceConfig(participant.getInstanceName());
                instance.setHostName(participant.getHostname());
                instance.setPort(participant.getPort().toString());
                instance.setInstanceEnabled(true);

                if (participant.getTags() != null) {
                    participant.getTags().forEach(tag -> {
                        instance.addTag(tag);
                    });
                }
                if(StringUtils.hasText(participant.getDomain())) {
                    instance.setDomain(participant.getDomain());
                }
                if(StringUtils.hasText(participant.getZoneId())) {
                    instance.setZoneId(participant.getZoneId());
                }
                try {
                admin.addInstance(clusterName, instance);
                } catch(Exception e) {
                   
                }
                logger.info("Added instance {} to cluster {}", participant.getInstanceName(), config.getClusterName());
            });

            // Assumes full-auto. Might decide to implement other...
            FullAutoModeISBuilder idealStateBuilder = new FullAutoModeISBuilder(config.getResourceName());
           idealStateBuilder.setStateModel(LeaderFollowerSMD.name)
                .setNumReplica(3);
            idealStateBuilder.add(config.getResourceName());

            //org.apache.helix.zookeeper.datamodel.ZNRecord resourceRecord = new ZNRecord(config.getResourceName());
            //Set the timeout values
            //StateTransitionTimeoutConfig stateTransitionTimeoutConfig = new StateTransitionTimeoutConfig(resourceRecord);
            //stateTransitionTimeoutConfig.setStateTransitionTimeout(States.FOLLOWER.name(), States.LEADER.name(), 300);
            ResourceConfig.Builder resourceConfigBuilder = new ResourceConfig.Builder(config.getResourceName())
                //.setStateTransitionTimeoutConfig(stateTransitionTimeoutConfig)
                //.setRebalanceConfig(new RebalanceConfig(resourceRecord))
                .setNumPartitions(0)
                .setNumReplica(3)
                .setHelixEnabled(true);
            
            //resourceConfig.getRebalanceConfig().setRebalanceMode(RebalanceMode.FULL_AUTO);
            
            logger.info("Adding resource {} to cluster {}", config.getResourceName(), config.getClusterName());
            admin.addResourceWithWeight(clusterName, idealStateBuilder.build(), resourceConfigBuilder.build());

            //admin.addResource(clusterName, config.getResourceName(), builder.build());
        } finally {
            admin.close();
        }

    }
}
