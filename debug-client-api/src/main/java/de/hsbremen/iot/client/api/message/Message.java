package de.hsbremen.iot.client.api.message;

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
