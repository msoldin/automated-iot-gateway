package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceService;

import java.util.List;
import java.util.Optional;

public class DeviceServiceProxy implements DeviceService {

    private final DeviceService deviceService;

    public DeviceServiceProxy(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public List<Device> getDevices() {
        return this.deviceService.getDevices();
    }

    @Override
    public List<Device> getNotPrivilegedDevices() {
        return this.deviceService.getNotPrivilegedDevices();
    }

    @Override
    public Optional<Device> getDevice(String deviceId) {
        return this.deviceService.getDevice(deviceId);
    }
}
