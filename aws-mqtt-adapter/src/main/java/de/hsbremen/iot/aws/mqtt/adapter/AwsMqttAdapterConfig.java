package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AwsMqttAdapterConfig extends Config {

    private AwsMqttClientConfig mqttConnection;

    private AwsRegistrationHandlerConfig registrationHandler;

}
