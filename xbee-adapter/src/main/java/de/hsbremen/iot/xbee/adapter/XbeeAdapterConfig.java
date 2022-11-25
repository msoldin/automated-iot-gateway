package de.hsbremen.iot.xbee.adapter;

import de.hsbremen.iot.gateway.api.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class XbeeAdapterConfig extends Config {

    private String port;

    private int baudRate;

}
