package de.hsbremen.iot.mqtt.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MqttClientAdapterConfig {

    private String serverUri;

    private String clientId;

    private String gatewayHostname;

}
