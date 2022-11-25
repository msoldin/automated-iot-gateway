package de.hsbremen.iot.mqtt.client;

import de.hsbremen.iot.client.impl.DebugClientConfig;
import de.hsbremen.iot.client.impl.DebugDeviceConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DebugMqttClientConfig {

    private MqttClientAdapterConfig mqtt;

    private DebugDeviceConfig debugDevice;

    private DebugClientConfig debugClient;

}
