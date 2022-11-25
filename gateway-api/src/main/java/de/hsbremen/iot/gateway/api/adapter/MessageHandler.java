package de.hsbremen.iot.gateway.api.adapter;

import de.hsbremen.iot.gateway.api.message.Message;

public interface MessageHandler extends Runnable {

    void start();

    void stop();

    int getSentQueueSize();

    int getAndResetMessageCount();

    void handle(Message message);

}
