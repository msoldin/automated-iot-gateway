package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.config.MailConfig;

public class TestMailConfig extends MailConfig {

    public TestMailConfig() {
        this.setPort(557);
        this.setHost("testHost");
        this.setAuthEnabled(false);
        this.setTlsEnabled(false);
        this.setUsername("test");
        this.setPassword("test");
        this.setSendTo("test");
        this.setSendFrom("test");
    }

}
