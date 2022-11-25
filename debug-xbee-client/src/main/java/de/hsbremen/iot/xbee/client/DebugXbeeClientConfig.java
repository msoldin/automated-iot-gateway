package de.hsbremen.iot.xbee.client;

import de.hsbremen.iot.client.impl.DebugClientConfig;
import de.hsbremen.iot.client.impl.DebugDeviceConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugXbeeClientConfig {

    private DebugDeviceConfig debugDevice;

    private DebugClientConfig debugClient;

    private XbeeClientAdapterConfig xbee;

}
