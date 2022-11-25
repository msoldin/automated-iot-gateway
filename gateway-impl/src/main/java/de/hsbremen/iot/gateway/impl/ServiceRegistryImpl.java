package de.hsbremen.iot.gateway.impl;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.InternalAdapterService;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.device.InternalDeviceService;
import de.hsbremen.iot.gateway.api.exception.InternalExceptionService;
import de.hsbremen.iot.gateway.api.http.HttpService;
import de.hsbremen.iot.gateway.api.mail.MailService;
import de.hsbremen.iot.gateway.api.message.InternalMessageService;
import de.hsbremen.iot.gateway.api.resource.ResourceService;
import de.hsbremen.iot.gateway.impl.adapter.AdapterServiceImpl;
import de.hsbremen.iot.gateway.impl.config.ConfigServiceImpl;
import de.hsbremen.iot.gateway.impl.device.DeviceServiceImpl;
import de.hsbremen.iot.gateway.impl.exception.ExceptionServiceImpl;
import de.hsbremen.iot.gateway.impl.http.HttpServiceImpl;
import de.hsbremen.iot.gateway.impl.mail.MailServiceImpl;
import de.hsbremen.iot.gateway.impl.message.MessageServiceImpl;
import de.hsbremen.iot.gateway.impl.resource.ResourceServiceImpl;

public class ServiceRegistryImpl implements InternalServiceRegistry {

    private static final String ID = "ServiceRegistry";

    private final InternalConfigService configService;

    private final MailService mailService;

    private final InternalExceptionService exceptionService;

    private final HttpService httpService;

    private final InternalMessageService messageService;

    private final InternalDeviceService deviceService;

    private final InternalAdapterService adapterService;

    private final ResourceService resourceService;

    public ServiceRegistryImpl() {
        this.configService = new ConfigServiceImpl(this);
        this.mailService = new MailServiceImpl(this);
        this.exceptionService = new ExceptionServiceImpl(this);
        this.httpService = new HttpServiceImpl(this);
        this.messageService = new MessageServiceImpl(this);
        this.deviceService = new DeviceServiceImpl(this);
        this.adapterService = new AdapterServiceImpl(this);
        this.resourceService = new ResourceServiceImpl(this);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        this.configService.start();
        this.mailService.start();
        this.exceptionService.start();
        this.httpService.start();
        this.messageService.start();
        this.deviceService.start();
        this.adapterService.start();
        this.resourceService.start();
    }

    @Override
    public void stop() {
        this.configService.stop();
        this.mailService.stop();
        this.exceptionService.stop();
        this.httpService.stop();
        this.messageService.stop();
        this.deviceService.stop();
        this.adapterService.stop();
        this.resourceService.stop();
    }

    @Override
    public HttpService httpService() {
        return this.httpService;
    }

    @Override
    public InternalMessageService messageService() {
        return this.messageService;
    }

    @Override
    public InternalExceptionService exceptionService() {
        return this.exceptionService;
    }

    @Override
    public ResourceService resourceService() {
        return this.resourceService;
    }

    @Override
    public InternalDeviceService deviceService() {
        return this.deviceService;
    }

    @Override
    public InternalAdapterService adapterService() {
        return this.adapterService;
    }

    @Override
    public MailService mailService() {
        return this.mailService;
    }

    @Override
    public InternalConfigService configService() {
        return this.configService;
    }

}
