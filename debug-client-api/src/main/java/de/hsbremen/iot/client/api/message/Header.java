package de.hsbremen.iot.client.api.message;

import lombok.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Header {

    private final static Logger logger = LogManager.getLogger();

    public static byte[] PREAMBLE;

    public static byte[] VERSION;

    public static int MIN_HEADER_SIZE = 18;

    public static int MAX_HEADER_SIZE = 530;

    static {
        try {
            PREAMBLE = Hex.decodeHex("FF6EDC4D");
            VERSION = Hex.decodeHex("00000001");
        } catch (DecoderException e) {
            logger.error(e);
            System.exit(-1);
        }
    }

    private MessageType messageType;

    private Priority priority;

    private boolean compressed;

    private String from;

    private String to;

}
