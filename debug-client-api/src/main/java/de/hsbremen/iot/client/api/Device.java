package de.hsbremen.iot.client.api;

import java.util.Map;

public interface Device {

    String deviceId();

    int getMaxQoS();

    int getCurrentQoS();

    void setCurrentQos(int qos);

    boolean isPrivileged();

    boolean highPriority();

    Map<String, Object> getState();

}
