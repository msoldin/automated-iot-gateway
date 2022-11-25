package de.hsbremen.iot.client.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DebugDeviceConfig {

    private String deviceId;

    private int maxQoS;

    private int currentQoS;

    private boolean privileged;

    private boolean highPriority;

}
