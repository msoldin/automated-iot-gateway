package de.hsbremen.iot.client.api;

import de.hsbremen.iot.client.api.message.Message;

import java.util.function.Consumer;

public interface ClientAdapter {

    void send(Message message);

    void send(byte[] message);

    void onReceive(Consumer<Message> messageConsumer);

    void start();

    void stop();

}
