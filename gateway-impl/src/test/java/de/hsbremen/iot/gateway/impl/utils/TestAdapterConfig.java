package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.AdapterConfig;

import java.util.ArrayList;

public class TestAdapterConfig extends AdapterConfig {

    public TestAdapterConfig() {
        this.setAdapterId(TestHelper.FROM_SERVICE);
        this.setSubscriber(new ArrayList<>());
    }

}
