package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.MessageServiceConfig;

public class TestMessageServiceConfig extends MessageServiceConfig {

    public TestMessageServiceConfig() {
        this.setMessageInterceptorEnabled(true);
        this.setUnzipIncomingMessages(true);
    }

}
