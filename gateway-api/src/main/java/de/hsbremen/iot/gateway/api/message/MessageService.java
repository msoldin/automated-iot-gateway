package de.hsbremen.iot.gateway.api.message;

public interface MessageService {

    void publish(Message message);

    MessageParser getMessageParser();

}
