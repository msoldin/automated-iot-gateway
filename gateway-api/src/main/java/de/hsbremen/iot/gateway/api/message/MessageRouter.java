package de.hsbremen.iot.gateway.api.message;

import java.util.Queue;

public interface MessageRouter {

    void handle(Queue<Message> messages);

}
