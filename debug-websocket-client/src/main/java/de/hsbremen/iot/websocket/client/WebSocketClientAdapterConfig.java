package de.hsbremen.iot.websocket.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketClientAdapterConfig {

    private int port;

    private String host;

    private String uri;

}
