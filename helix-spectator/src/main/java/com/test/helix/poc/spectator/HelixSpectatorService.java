package com.test.helix.poc.spectator;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.api.listeners.RoutingTableChangeListener;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.spectator.RoutingTableProvider;
import org.apache.helix.spectator.RoutingTableSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.test.helix.poc.statemodel.LeaderFollowerSMD;

@Service
public class HelixSpectatorService implements RoutingTableChangeListener {
    private final Logger logger = LoggerFactory.getLogger(HelixSpectatorService.class);

    private HelixManager manager = null;

    private final AtomicBoolean onLeaderZone = new AtomicBoolean(true);
    private final SpectatorConfig config;
    private RoutingTableProvider routingTableProvider;

    public HelixSpectatorService(SpectatorConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void start() throws Exception {
        logger.info("Starting Helix participant {} in cluster {} for resource {}",config.getInstanceName(), config.getClusterName(), config.getResourceName());
        manager = HelixManagerFactory.getZKHelixManager(
                config.getClusterName(), 
                config.getInstanceName(),
                InstanceType.SPECTATOR, 
                config.getZookeeperAddress());
        manager.connect();

        routingTableProvider = new RoutingTableProvider();
        routingTableProvider.addRoutingTableChangeListener(this, config);
        manager.addExternalViewChangeListener(routingTableProvider);
        logger.info("Started Helix participant {} in cluster {} for resource {}",config.getInstanceName(), config.getClusterName(), config.getResourceName());
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down Helix participant");
        if (manager != null) {
            try {
                routingTableProvider.shutdown();
            } finally {
                manager.disconnect();
            }
        }
    }

    /**
     * This method will let the caller know his application is running on the leader side or the follower
     * @return
     */
    public AtomicBoolean getOnLeaderZone() {
        return onLeaderZone;
    }

    @SuppressWarnings("deprecation")
    public void onRoutingTableChange(RoutingTableSnapshot routingTableSnapshot, Object ctx) {
        final String resource = config.getResourceName();

        logger.info("Helix routing table changed for resource {} in cluster {}", resource, config.getClusterName());

        Collection<InstanceConfig> instances = routingTableSnapshot.getInstancesForResource(resource,
                LeaderFollowerSMD.States.LEADER.name());
        if (instances != null && !instances.isEmpty()) {
            logger.info(" -> Retrieved  {} instances", instances.size());
            for (InstanceConfig instanceConfig : instances) {

                logger.info(
                        " -> Routing table changed for resource {} and instance {} ({}) with tags {} in domain {}, zone {}",
                        resource, instanceConfig.getInstanceName(), instanceConfig.getId(), instanceConfig.getTags(),
                        instanceConfig.getDomainAsString(), instanceConfig.getZoneId());

                // Really dummy, does not check if we have more than one (should never be the case!!)
                // If our domain (configured) matches the domain of the current leader, then this spectator is running
                // on the side which is "Leader"...  Otherwise false
                onLeaderZone.set(config.getDomain().equalsIgnoreCase(instanceConfig.getDomainAsString()));
            }
        } else {
            logger.info(" -> No instances for resource: {} in cluster {}", resource, config.getClusterName());
            onLeaderZone.set(Boolean.FALSE);
        }
    }
}
