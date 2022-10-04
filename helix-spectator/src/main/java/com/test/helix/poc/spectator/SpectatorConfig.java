package com.test.helix.poc.spectator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("helix.spectator")
public class SpectatorConfig {

    private String zookeeperAddress;
    private String clusterName;
    private String resourceName;
    private String instanceName;
    private String domain;

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String name) {
        this.instanceName = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
