package de.hsbremen.iot.client.api;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class DeviceQoSChangedPayload {

    private int currentQos;

}
