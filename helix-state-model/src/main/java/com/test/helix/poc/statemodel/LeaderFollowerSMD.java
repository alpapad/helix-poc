package com.test.helix.poc.statemodel;

import org.apache.helix.HelixDefinedState;
import org.apache.helix.model.StateModelDefinition;

/**
 * Helix built-in Master-slave state model definition
 */
public final class LeaderFollowerSMD extends StateModelDefinition {
    public static final String name = "LeaderFollower";

    public enum States {
        LEADER, FOLLOWER, OFFLINE
    }

    public LeaderFollowerSMD() {
        super(build().getRecord());
    }

    /**
     * Build Master-slave state model definition
     * 
     * @return
     */
    public static StateModelDefinition build() {
        StateModelDefinition.Builder builder = new StateModelDefinition.Builder(name);
        // init state
        builder.initialState(States.OFFLINE.name());

        // add states
        builder.addState(States.LEADER.name(), 0);
        builder.addState(States.FOLLOWER.name(), 1);
        builder.addState(States.OFFLINE.name(), 2);
        for (HelixDefinedState state : HelixDefinedState.values()) {
            builder.addState(state.name());
        }

        // add transitions
        builder.addTransition(States.LEADER.name(), States.FOLLOWER.name(), 0);
        builder.addTransition(States.FOLLOWER.name(), States.LEADER.name(), 1);
        builder.addTransition(States.OFFLINE.name(), States.FOLLOWER.name(), 2);
        builder.addTransition(States.FOLLOWER.name(), States.OFFLINE.name(), 3);
        builder.addTransition(States.OFFLINE.name(), HelixDefinedState.DROPPED.name());

        // bounds
        builder.upperBound(States.LEADER.name(), 1);
        builder.dynamicUpperBound(States.FOLLOWER.name(), "R");  //<- dependent on the resource definition
        //builder.dynamicUpperBound(States.FOLLOWER.name(), "N"); // <- in each node, one
        return builder.build();
    }
}
