package de.hsbremen.iot.gateway.api.device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends AutoCloseable{

    List<Device> findAll();

    List<Device> findNotPrivilegedDevices();

    Optional<Device> findByDeviceId(String deviceId);

    void save(Device device);

}
