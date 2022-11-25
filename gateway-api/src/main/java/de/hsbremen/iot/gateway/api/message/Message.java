package de.hsbremen.iot.gateway.api.message;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Message {

    private Header header;

    private byte[] payload;

}

