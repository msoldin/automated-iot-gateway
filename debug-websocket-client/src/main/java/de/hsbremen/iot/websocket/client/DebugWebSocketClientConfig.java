package de.hsbremen.iot.websocket.client;

import de.hsbremen.iot.client.impl.DebugClientConfig;
import de.hsbremen.iot.client.impl.DebugDeviceConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugWebSocketClientConfig {

    private DebugDeviceConfig debugDevice;

    private DebugClientConfig debugClient;

    private WebSocketClientAdapterConfig websocket;

}
