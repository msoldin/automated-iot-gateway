package de.hsbremen.iot.mqtt.adapter;

import de.hsbremen.iot.gateway.api.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MqttAdapterConfig extends Config {

    private String serverUri;

    private boolean cleanSession;

    private boolean automaticReconnect;

}
