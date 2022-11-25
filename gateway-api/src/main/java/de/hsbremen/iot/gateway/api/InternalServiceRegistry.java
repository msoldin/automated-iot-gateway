package de.hsbremen.iot.gateway.api;

import de.hsbremen.iot.gateway.api.adapter.InternalAdapterService;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.device.InternalDeviceService;
import de.hsbremen.iot.gateway.api.exception.InternalExceptionService;
import de.hsbremen.iot.gateway.api.http.HttpService;
import de.hsbremen.iot.gateway.api.mail.MailService;
import de.hsbremen.iot.gateway.api.message.InternalMessageService;
import de.hsbremen.iot.gateway.api.resource.ResourceService;

public interface InternalServiceRegistry extends ServiceRegistry, Service {

    HttpService httpService();

    InternalMessageService messageService();

    InternalExceptionService exceptionService();

    ResourceService resourceService();

    InternalDeviceService deviceService();

    InternalAdapterService adapterService();

    MailService mailService();

    InternalConfigService configService();

}
