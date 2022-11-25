package de.hsbremen.iot.gateway.api.device;

import java.util.List;
import java.util.Optional;

public interface DeviceCache {

    void put(Device device);

    void putAll(List<Device> devices);

    Optional<Device> get(String deviceId);
}
