package de.hsbremen.iot.gateway.api.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AdapterUsage {

    private String adapterId;

    private int sentQueueSize;

    private int sentMessageCount;

    private int receivedMessageCount;

}
