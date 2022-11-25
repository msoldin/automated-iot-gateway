package de.hsbremen.iot.gateway.api.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailConfig extends Config {

    private int port;

    private String host;

    private boolean authEnabled;

    private boolean tlsEnabled;

    private String username;

    private String password;

    private String sendTo;

    private String sendFrom;

}
