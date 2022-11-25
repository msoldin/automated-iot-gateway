package de.hsbremen.iot.gateway.api.message;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Header {

    public static byte[] PREAMBLE = new byte[]{-1, 110, -36, 77};

    public static byte[] VERSION = new byte[]{0, 0, 0, 1};

    public static int MIN_HEADER_SIZE = 18;

    public static int MAX_HEADER_SIZE = 530;

    private String fromService;

    private MessageType type;

    private Priority priority;

    private boolean compressed;

    private String from;

    private String to;

}
