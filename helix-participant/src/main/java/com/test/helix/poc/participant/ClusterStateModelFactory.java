package com.test.helix.poc.participant;

import org.apache.helix.participant.statemachine.StateModelFactory;

public class ClusterStateModelFactory extends StateModelFactory<ClusterStateModel> {
    @Override
    public ClusterStateModel createNewStateModel(String resourceName, String partitionName) {
        return new ClusterStateModel(resourceName, partitionName);
    }
}