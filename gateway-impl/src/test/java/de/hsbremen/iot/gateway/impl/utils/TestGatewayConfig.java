package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.GatewayConfig;
import de.hsbremen.iot.gateway.api.config.VertxConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGatewayConfig extends GatewayConfig {

    public TestGatewayConfig() {
        this.setMail(new TestMailConfig());
        this.setVertx(new VertxConfig());
        this.setAdapters(new ArrayList<>(List.of(new TestAdapterConfig())));
        this.setMessaging(new TestMessageServiceConfig());
        this.setMonitoring(new TestResourceServiceConfig());
        this.setHostname(TestHelper.HOSTNAME);
        this.setMappedAdapters(new HashMap<>(Map.of(TestHelper.FROM_SERVICE, new TestAdapterConfig())));
    }

}
