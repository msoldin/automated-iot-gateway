package de.hsbremen.iot.gateway.impl.resource;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.ResourceServiceConfig;
import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceState;
import de.hsbremen.iot.gateway.api.message.*;
import de.hsbremen.iot.gateway.api.resource.ResourceScheduler;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class ResourceSchedulerImpl implements ResourceScheduler {

    private static final Logger logger = LogManager.getLogger();

    private long timeSinceHighResourceConsumption;

    private long timeSinceLastHighResourceConsumption;

    private final InternalServiceRegistry serviceRegistry;

    private final Queue<ResourceUsageMeasurement> measurements;

    public ResourceSchedulerImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.measurements = new CircularFifoQueue<>(10);
        this.timeSinceHighResourceConsumption = 0;
        this.timeSinceLastHighResourceConsumption = 0;
    }

    @Override
    public void handle(ResourceUsageMeasurement measurement) {
        this.measurements.add(measurement);
        MessageFilter messageFilter = this.serviceRegistry.messageService().getMessageFilter();
        ResourceServiceConfig config = this.serviceRegistry.configService().getConfig().getMonitoring();

        double memoryUsage = this.measurements
                .stream()
                .mapToDouble(this::getCurrentMemoryConsumption)
                .average()
                .orElse(0);

        double cpuUsage = this.measurements
                .stream()
                .mapToDouble(ResourceUsageMeasurement::getCpuUsageTotal)
                .average()
                .orElse(0);

        boolean highMemoryUsage = this.isHigherThan(memoryUsage,
                config.getPrimaryMemoryThreshold(),
                config.getSecondaryMemoryThreshold(),
                config.getTertiaryMemoryThreshold());

        boolean highCpuUsage = this.isHigherThan(cpuUsage,
                config.getPrimaryCpuThreshold(),
                config.getSecondaryCpuThreshold(),
                config.getTertiaryCpuThreshold());

        if (highMemoryUsage || highCpuUsage) {
            this.timeSinceLastHighResourceConsumption = System.currentTimeMillis();
            this.serviceRegistry.deviceService()
                    .getNotPrivilegedDevices()
                    .stream()
                    .filter(device -> device.getCurrentQos() != 0 && device.getState() == DeviceState.CONNECTED)
                    .forEach(this::publishQoSReduceMessage);
            if (this.timeSinceHighResourceConsumption == 0) {
                logger.info("High resource consumption detected!");
                this.timeSinceHighResourceConsumption = System.currentTimeMillis();
            } else if (this.timeSinceHighResourceConsumption < (System.currentTimeMillis() - 10 * config.getInterval())) {

                if ((config.getPrimaryMemoryThreshold() <= memoryUsage || config.getPrimaryCpuThreshold() <= cpuUsage) &&
                        !messageFilter.isRegistrationFilter()) {
                    logger.info("Activating RegistrationFilter, no more registrations allowed!");
                    messageFilter.setRegistrationFilter(true);
                }

                if ((config.getSecondaryMemoryThreshold() <= memoryUsage || config.getSecondaryCpuThreshold() <= cpuUsage)
                        && !messageFilter.isBestEffortFilter()) {
                    logger.info("Activating BestEffort, no more best effort messages allowed!");
                    messageFilter.setBestEffortFilter(true);
                }

                if ((config.getTertiaryMemoryThreshold() <= memoryUsage || config.getTertiaryCpuThreshold() <= cpuUsage)
                        && messageFilter.isHighPriorityFilter()) {
                    logger.info("Activating HighPriorityFilter, no more high priority messages allowed!");
                    messageFilter.setHighPriorityFilter(true);
                }
            }
        } else if (timeSinceLastHighResourceConsumption != 0
                && this.timeSinceLastHighResourceConsumption < (System.currentTimeMillis() - 10 * config.getInterval())) {
            this.timeSinceHighResourceConsumption = 0;
            this.timeSinceLastHighResourceConsumption = 0;
            messageFilter.setRegistrationFilter(false);
            messageFilter.setBestEffortFilter(false);
            messageFilter.setHighPriorityFilter(false);
        }
    }

    private void publishQoSReduceMessage(Device device) {
        Message message = Message.builder()
                .header(Header.builder()
                        .from(this.serviceRegistry.configService().getConfig().getHostname())
                        .to(device.getDeviceId())
                        .fromService(ResourceServiceImpl.ID)
                        .priority(Priority.HIGH_PRIORITY)
                        .type(MessageType.QOS_REDUCE)
                        .compressed(false)
                        .build())
                .payload(null)
                .build();
        this.serviceRegistry.messageService().publish(message);
    }

    private double getCurrentMemoryConsumption(ResourceUsageMeasurement measurement) {
        return (double) measurement.getHeapMemoryUsage().getUsed() / measurement.getHeapMemoryUsage().getMax();
    }

    private boolean isHigherThan(double currentConsumption, double... values) {
        for (double i : values) {
            if (i < currentConsumption)
                return true;
        }
        return false;
    }
}
