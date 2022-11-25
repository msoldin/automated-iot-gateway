package de.hsbremen.iot.gateway.api.message;

public interface PayloadParser {

    <T> T parse(Message message, Class<T> tClass);

}
