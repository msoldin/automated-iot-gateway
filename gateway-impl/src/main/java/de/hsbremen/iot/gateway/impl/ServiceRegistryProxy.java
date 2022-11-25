package de.hsbremen.iot.gateway.impl;

import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.AdapterService;
import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.device.DeviceService;
import de.hsbremen.iot.gateway.api.exception.ExceptionService;
import de.hsbremen.iot.gateway.api.message.MessageService;
import de.hsbremen.iot.gateway.impl.adapter.AdapterServiceProxy;
import de.hsbremen.iot.gateway.impl.config.ConfigServiceProxy;
import de.hsbremen.iot.gateway.impl.device.DeviceServiceProxy;
import de.hsbremen.iot.gateway.impl.exception.ExceptionServiceProxy;
import de.hsbremen.iot.gateway.impl.message.MessageServiceProxy;

public class ServiceRegistryProxy implements ServiceRegistry {

    private final MessageServiceProxy messageService;

    private final ExceptionServiceProxy exceptionService;

    private final DeviceServiceProxy deviceService;

    private final AdapterServiceProxy adapterService;

    private final ConfigServiceProxy configService;

    public ServiceRegistryProxy(ServiceRegistry serviceRegistry) {
        this.messageService = new MessageServiceProxy(serviceRegistry.messageService());
        this.exceptionService = new ExceptionServiceProxy(serviceRegistry.exceptionService());
        this.deviceService = new DeviceServiceProxy(serviceRegistry.deviceService());
        this.adapterService = new AdapterServiceProxy(serviceRegistry.adapterService());
        this.configService = new ConfigServiceProxy(serviceRegistry.configService());
    }

    @Override
    public MessageService messageService() {
        return this.messageService;
    }

    @Override
    public ExceptionService exceptionService() {
        return this.exceptionService;
    }

    @Override
    public DeviceService deviceService() {
        return this.deviceService;
    }

    @Override
    public AdapterService adapterService() {
        return this.adapterService;
    }

    @Override
    public ConfigService configService() {
        return this.configService;
    }
}
