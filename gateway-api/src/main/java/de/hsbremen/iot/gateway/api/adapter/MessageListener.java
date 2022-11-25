package de.hsbremen.iot.gateway.api.adapter;

public interface MessageListener {

    void start();

    void stop();

    int getAndResetMessageCount();

}
