package de.hsbremen.iot.gateway.api;

import de.hsbremen.iot.gateway.api.adapter.AdapterService;
import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.device.DeviceService;
import de.hsbremen.iot.gateway.api.exception.ExceptionService;
import de.hsbremen.iot.gateway.api.message.MessageService;

public interface ServiceRegistry {

    MessageService messageService();

    ExceptionService exceptionService();

    DeviceService deviceService();

    AdapterService adapterService();

    ConfigService configService();

}
