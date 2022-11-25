package de.hsbremen.iot.gateway.api.adapter;

import de.hsbremen.iot.gateway.api.device.Device;

public interface RegistrationHandler {

    void handle(Device device);

}
