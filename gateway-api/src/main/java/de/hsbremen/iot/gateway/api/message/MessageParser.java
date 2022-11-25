package de.hsbremen.iot.gateway.api.message;

import de.hsbremen.iot.gateway.api.exception.MessageParserException;

public interface MessageParser {

    byte[] parse(Message message) throws MessageParserException;

    byte[] parseToLegacy(Message message) throws MessageParserException;

    byte[] parseForLowBandwidth(Message message) throws MessageParserException;

    Message parse(byte[] message, String fromService) throws MessageParserException;

    Message parse(byte[] message, String fromService, String fromDevice) throws MessageParserException;

    boolean isLegacy(byte[] message) throws MessageParserException;

    Message parseLegacy(byte[] message, String fromService, String fromDevice) throws MessageParserException;

}
