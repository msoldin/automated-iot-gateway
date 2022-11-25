package de.hsbremen.iot.client.impl;

import de.hsbremen.iot.client.api.Device;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class DebugDevice implements Device {

    private final int maxQoS;

    private final String deviceId;

    private final boolean privileged;

    private final boolean highPriority;

    private final Map<String, Object> state;

    private int currentQoS;

    public DebugDevice(DebugDeviceConfig config) {
        this.deviceId = config.getDeviceId();
        this.maxQoS = config.getMaxQoS();
        this.currentQoS = config.getCurrentQoS();
        this.privileged = config.isPrivileged();
        this.highPriority = config.isHighPriority();
        this.state = new HashMap<>();
    }

    @Override
    public String deviceId() {
        return this.deviceId;
    }

    @Override
    public int getMaxQoS() {
        return this.maxQoS;
    }

    @Override
    public int getCurrentQoS() {
        return this.currentQoS;
    }

    @Override
    public void setCurrentQos(int qos) {
        this.currentQoS = qos;
    }

    @Override
    public boolean isPrivileged() {
        return this.privileged;
    }

    @Override
    public boolean highPriority() {
        return this.highPriority;
    }

    @Override
    public Map<String, Object> getState() {
        this.state.put("sentTime", LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli());
        Map<String, Object> reported = new HashMap<>();
        reported.put("reported", this.state);
        Map<String, Object> awsState = new HashMap<>();
        awsState.put("state", reported);
        return awsState;
    }
}
