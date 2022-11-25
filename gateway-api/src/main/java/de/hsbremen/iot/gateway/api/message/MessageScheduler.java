package de.hsbremen.iot.gateway.api.message;

import java.util.Queue;

public interface MessageScheduler {

    boolean isEmpty();

    int getSize();

    void publish(Message message);

    Queue<Message> schedule();

}
