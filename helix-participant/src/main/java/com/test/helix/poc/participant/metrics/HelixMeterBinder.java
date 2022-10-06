package com.test.helix.poc.participant.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * See https://helix.apache.org/1.0.4-docs/Metrics.html for a list of mbeans and metrics available
 * 
 */
public class HelixMeterBinder implements MeterBinder {

    private final String instanceName;
    private final String cluster;
    private final  Tags tags;
    private ParticipantMessageMetricBridge participantMessageMetrics;
    
    public HelixMeterBinder(final String cluster, final String instanceName) {
        this.cluster = cluster;
        this.instanceName = instanceName;
        tags = Tags.of("cluster", this.cluster, "instance",this.instanceName);
        participantMessageMetrics = new ParticipantMessageMetricBridge(instanceName);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        participantMessageMetrics.register(registry, tags);
    }

}
