package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import de.hsbremen.iot.gateway.api.message.MessageService;

public class MessageServiceProxy implements MessageService {

    private final MessageService messageService;

    public MessageServiceProxy(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void publish(Message message) {
        this.messageService.publish(message);
    }

    @Override
    public MessageParser getMessageParser() {
        return this.messageService.getMessageParser();
    }
}
