package com.test.helix.poc.participant;

import static com.test.helix.poc.statemodel.States.*;

import org.apache.helix.NotificationContext;
import org.apache.helix.model.Message;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StateModelInfo(initialState = OFFLINE, states = { LEADER, FOLLOWER, ERROR })
public class ClusterStateModel extends StateModel {
    private final static Logger logger = LoggerFactory.getLogger(ClusterStateModel.class);

    private final String resourceName;
    private final String partitionName;
    
    
    public ClusterStateModel(String resourceName, String partitionName) {
        super();
        this.resourceName = resourceName;
        this.partitionName = partitionName;
    }

    @Transition(to = FOLLOWER, from = OFFLINE)
    public void onBecomeFollowerFromOffline(Message message, NotificationContext context) {
        log(message, context);
    }

    @Transition(to = LEADER, from = FOLLOWER)
    public void onBecomeLeaderFromFollower(Message message, NotificationContext context) {
        log(message, context);
    }

    @Transition(to = FOLLOWER, from = LEADER)
    public void onBecomeFollowerFromLeader(Message message, NotificationContext context) {
        log(message, context);
    }

    @Transition(to = OFFLINE, from = FOLLOWER)
    public void onBecomeOfflineFromSlave(Message message, NotificationContext context) {
        log(message, context);
    }

    @Transition(to = DROPPED, from = OFFLINE)
    public void onBecomeDroppedFromOffline(Message message, NotificationContext context) {
        log(message, context);
    }

    @Transition(to = OFFLINE, from = ERROR)
    public void onBecomeOfflineFromError(Message message, NotificationContext context) {
        log(message, context);
    }

    @Override
    @Transition(to = DROPPED, from = ERROR)
    public void onBecomeDroppedFromError(Message message, NotificationContext context) {
        log(message, context);
    }

    @Override
    public void reset() {
        logger.error("Default  StateModel.reset() invoked");
    }

    private void log(Message message, NotificationContext context) {
        String partitionName = message.getPartitionName();
        String instanceName = message.getTgtName();
        String fromState = message.getFromState();
        String toState = message.getToState();
        String sessId = message.getSrcSessionId();
        String clusterName = message.getSrcClusterName();
        logger.error("Cluster {}, session: {}, instance: {}, resource {}, partition: {} ({}): Transition from '{}' to '{}'", clusterName,
                sessId, instanceName, resourceName, partitionName, this.partitionName, fromState, toState);
    }
}