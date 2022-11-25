package de.hsbremen.iot.gateway.api.device;

import de.hsbremen.iot.gateway.api.message.Message;

public interface DeviceFactory {

    Device createDevice(Message message);

}
