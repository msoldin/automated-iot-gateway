package de.hsbremen.iot.gateway.api.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MessageServiceUsage {

    private int queueSize;

    private int sentMessageCount;

    private int receivedMessageCount;

}
