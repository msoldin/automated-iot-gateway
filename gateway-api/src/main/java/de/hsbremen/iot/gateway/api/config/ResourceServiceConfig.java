package de.hsbremen.iot.gateway.api.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceServiceConfig extends Config {

    private long interval;

    private double primaryMemoryThreshold;

    private double secondaryMemoryThreshold;

    private double tertiaryMemoryThreshold;

    private double primaryCpuThreshold;

    private double secondaryCpuThreshold;

    private double tertiaryCpuThreshold;

}
