package de.hsbremen.iot.gateway.impl.resource;

import com.sun.management.OperatingSystemMXBean;
import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.resource.AdapterUsage;
import de.hsbremen.iot.gateway.api.resource.MessageServiceUsage;
import de.hsbremen.iot.gateway.api.resource.ResourceActuator;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceActuatorImpl implements ResourceActuator {

    private final InternalServiceRegistry serviceRegistry;

    private final MemoryMXBean memoryMXBean;

    private final OperatingSystemMXBean operatingSystemMXBean;

    public ResourceActuatorImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @Override
    public ResourceUsageMeasurement getResourceState() {
        ResourceUsageMeasurement.ResourceUsageMeasurementBuilder builder = ResourceUsageMeasurement.builder()
                .cpuUsageProcess(this.operatingSystemMXBean.getProcessCpuLoad())
                .cpuUsageTotal(this.operatingSystemMXBean.getSystemCpuLoad())
                .heapMemoryUsage(this.memoryMXBean.getHeapMemoryUsage())
                .nonHeapMemoryUsage(this.memoryMXBean.getNonHeapMemoryUsage())
                .messageServiceUsage(this.getMessageServiceUsage())
                .adapterUsages(this.getAdapterUsages())
                .interval(this.serviceRegistry.configService().getConfig().getMonitoring().getInterval())
                .systemTime(System.currentTimeMillis());
        return builder.build();
    }

    private MessageServiceUsage getMessageServiceUsage() {
        return new MessageServiceUsage(this.serviceRegistry.messageService().getMessageScheduler().getSize(),
                this.serviceRegistry.messageService().getAndResetSentMessageCount(),
                this.serviceRegistry.messageService().getAndResetReceivedMessageCount());
    }

    private List<AdapterUsage> getAdapterUsages() {
        return this.serviceRegistry.adapterService()
                .getAdapters()
                .values()
                .stream()
                .map(adapter -> {
                    int sentQueueSize = adapter.messageHandler().getSentQueueSize();
                    int sent = adapter.messageHandler().getAndResetMessageCount();
                    int received = adapter.messageListener().getAndResetMessageCount();
                    return new AdapterUsage(adapter.id(), sentQueueSize, sent, received);
                }).collect(Collectors.toList());
    }
}
