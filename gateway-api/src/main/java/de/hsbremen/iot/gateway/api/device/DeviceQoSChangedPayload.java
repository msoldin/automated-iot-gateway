package de.hsbremen.iot.gateway.api.device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceQoSChangedPayload {

    private int currentQos;

}
