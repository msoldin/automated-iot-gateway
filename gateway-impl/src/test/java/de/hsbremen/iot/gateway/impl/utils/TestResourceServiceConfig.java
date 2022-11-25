package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.ResourceServiceConfig;

public class TestResourceServiceConfig extends ResourceServiceConfig {

    public TestResourceServiceConfig() {
        this.setInterval(1000);
        this.setPrimaryMemoryThreshold(50);
        this.setSecondaryMemoryThreshold(70);
        this.setTertiaryMemoryThreshold(90);
        this.setPrimaryCpuThreshold(50);
        this.setSecondaryCpuThreshold(70);
        this.setTertiaryCpuThreshold(90);
    }

}
