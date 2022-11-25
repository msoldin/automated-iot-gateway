package de.hsbremen.iot.client.api;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class DeviceConnectPayload {

    private int maxQos;

    private int currentQos;

    private boolean privileged;

}
