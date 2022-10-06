package com.test.helix.poc.participant.metrics;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.helix.monitoring.mbeans.MBeanRegistrar;
import org.apache.helix.monitoring.mbeans.MonitorDomainNames;
import org.apache.helix.monitoring.mbeans.ParticipantMessageMonitor;
import org.apache.helix.monitoring.mbeans.ParticipantStatusMonitor;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.BaseUnits;

public class ParticipantMessageMetricBridge {

    private final MBeanServer mbs;
    private final ObjectName mbeanName;
    private final static double ZERO = Double.valueOf(0);

    public ParticipantMessageMetricBridge(final String instanceName) {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            // Stolen from ParticipantMessageMonitor.register
            String[] keyValuePairs = { ParticipantStatusMonitor.PARTICIPANT_KEY, instanceName, "MonitorType",
                    ParticipantMessageMonitor.class.getSimpleName() };

            mbeanName = MBeanRegistrar.buildObjectName(MonitorDomainNames.CLMParticipantReport.name(), keyValuePairs);
        } catch (MalformedObjectNameException ex) {
            throw new RuntimeException("Can not lookup ParticipantMessageMonitor", ex);
        }
    }

    public void register(MeterRegistry registry,  Tags tags) {
        FunctionCounter.builder("helix.participant.messages.received" , this,  this::getReceivedMessages)
        .tags(tags)
        .description("Received messages by Participant")
        .baseUnit(BaseUnits.MESSAGES)
        .register(registry);
    
    FunctionCounter.builder("helix.participant.messages.discarded" , this,  this::getDiscardedMessages)
        .tags(tags)
        .description("Received messages by Participant")
        .baseUnit(BaseUnits.MESSAGES)
        .register(registry);
    
    FunctionCounter.builder("helix.participant.messages.failed" , this,  this::getFailedMessages)
        .tags(tags)
        .description("Received messages by Participant")
        .baseUnit(BaseUnits.MESSAGES)
        .register(registry);
    
    Gauge.builder("helix.participant.messages.pending" , this,  this::getPendingMessages)
        .tags(tags)
        .description("Received messages by Participant")
        .baseUnit(BaseUnits.MESSAGES)
        .register(registry);
    
    FunctionCounter.builder("helix.participant.messages.completed" , this,  this::getCompletedMessages)
        .tags(tags)
        .description("Received messages by Participant")
        .baseUnit(BaseUnits.MESSAGES)
        .register(registry);
    }
    public double getReceivedMessages(ParticipantMessageMetricBridge b) {
        return this.getCounterValue("ReceivedMessages");
    }

    public double getDiscardedMessages(ParticipantMessageMetricBridge b) {
        return this.getCounterValue("DiscardedMessages");
    }

    public double getFailedMessages(ParticipantMessageMetricBridge b) {
        return this.getCounterValue("FailedMessages");
    }

    public double getPendingMessages(ParticipantMessageMetricBridge b) {
        return this.getCounterValue("PendingMessages");
    }

    public double getCompletedMessages(ParticipantMessageMetricBridge b) {
        return this.getCounterValue("CompletedMessages");
    }

    private double getCounterValue(String string) {
        Long value = getDynamicAttribute(string);
        if (value != null) {
            return value.doubleValue();
        }
        return ZERO;
    }

    private Long getDynamicAttribute(String attr) {
        if (mbs.isRegistered(mbeanName)) {
            try {
                return (Long) mbs.getAttribute(mbeanName, attr);
            } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

}
