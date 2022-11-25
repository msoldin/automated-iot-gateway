package de.hsbremen.iot.gateway.api.device;

import de.hsbremen.iot.gateway.api.Service;
import de.hsbremen.iot.gateway.api.message.Message;

public interface InternalDeviceService extends DeviceService, Service {

    void handle(Message message);

}
