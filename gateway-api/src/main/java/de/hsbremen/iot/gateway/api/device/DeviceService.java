package de.hsbremen.iot.gateway.api.device;

import java.util.List;
import java.util.Optional;

public interface DeviceService {

    List<Device> getDevices();

    List<Device> getNotPrivilegedDevices();

    Optional<Device> getDevice(String deviceId);

}
