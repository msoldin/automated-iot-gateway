package de.hsbremen.iot.client.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DebugClientConfig {

    private boolean legacy;

    private boolean roundTrip;

    private boolean lowBandwidth;

    private long initialDelay;

    private long reportInterval;

}
