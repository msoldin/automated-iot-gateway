package de.hsbremen.iot.gateway.api.resource;

import lombok.*;

import java.lang.management.MemoryUsage;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ResourceUsageMeasurement {

    private long interval;

    private long systemTime;

    private double cpuUsageTotal;

    private double cpuUsageProcess;

    private MemoryUsage heapMemoryUsage;

    private MemoryUsage nonHeapMemoryUsage;

    private MessageServiceUsage messageServiceUsage;

    private List<AdapterUsage> adapterUsages;

}
