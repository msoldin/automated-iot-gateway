package de.hsbremen.iot.gateway.api.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageServiceConfig extends Config{

    private boolean unzipIncomingMessages;

    private boolean messageInterceptorEnabled;

}
