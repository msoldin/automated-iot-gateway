package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AwsMqttClientConfig extends Config {

    private String clientId;

    private String endpoint;

    private String certPath;

    private String keyPath;

    private boolean deviceShadowEnabled;

}
