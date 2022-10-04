package com.test.helix.poc.setup;

import java.util.ArrayList;
import java.util.List;

import org.apache.helix.model.IdealState.RebalanceMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("helix.setup")
public class ClusterSetupConfig {

    private String zookeeperAddress;
    private String clusterName;
    private String resourceName;
    private boolean recreateIfExists = Boolean.FALSE;
    private boolean drop = Boolean.TRUE;;

    private RebalanceMode rebalanceMode = RebalanceMode.FULL_AUTO;

    private List<ClusterParticipantConfig> topology = new ArrayList<>();

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public RebalanceMode getRebalanceMode() {
        return rebalanceMode;
    }

    public void setRebalanceMode(RebalanceMode rebalanceMode) {
        this.rebalanceMode = rebalanceMode;
    }

    public List<ClusterParticipantConfig> getTopology() {
        return topology;
    }

    public void setTopology(List<ClusterParticipantConfig> participants) {
        this.topology = participants;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean isRecreateIfExists() {
        return recreateIfExists;
    }

    public void setRecreateIfExists(boolean recreateIfExists) {
        this.recreateIfExists = recreateIfExists;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

}
