package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.VertxConfig;

public class TestVertxConfig extends VertxConfig {

    public TestVertxConfig() {
        this.setPort(8080);
    }

}
