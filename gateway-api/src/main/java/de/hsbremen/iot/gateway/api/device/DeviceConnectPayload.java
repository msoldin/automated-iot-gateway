package de.hsbremen.iot.gateway.api.device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceConnectPayload {

    private int maxQos;

    private int currentQos;

    private boolean privileged;

}
