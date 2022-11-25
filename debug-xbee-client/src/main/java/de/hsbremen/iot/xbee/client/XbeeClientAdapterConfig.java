package de.hsbremen.iot.xbee.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XbeeClientAdapterConfig {

    private String port;

    private int baudRate;

    private String coordinator;

}
