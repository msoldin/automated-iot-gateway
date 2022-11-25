package de.hsbremen.iot.xbee.adapter;

import com.digi.xbee.api.XBeeDevice;
import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.*;
import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;

import java.util.Optional;

public class XbeeAdapter implements Adapter {

    private static final String ID = "xbee-adapter";

    private XBeeDevice xBeeDevice;

    private final ServiceRegistry serviceRegistry;

    private final XbeeMessageHandler xbeeMessageHandler;

    private final XbeeMessageListener xbeeMessageListener;

    public XbeeAdapter(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.xbeeMessageHandler = new XbeeMessageHandler(this);
        this.xbeeMessageListener = new XbeeMessageListener(this);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            ConfigService configService = this.serviceRegistry.configService();
            XbeeAdapterConfig xbeeAdapterConfig = configService.getConfigFromBase(ID, XbeeAdapterConfig.class);
            this.xBeeDevice = new XBeeDevice(xbeeAdapterConfig.getPort(), xbeeAdapterConfig.getBaudRate());
            this.xBeeDevice.open();
            this.xbeeMessageListener.start();
            this.xbeeMessageHandler.start();
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException(ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.xbeeMessageHandler.stop();
            this.xbeeMessageListener.stop();
            this.xBeeDevice.close();
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException(ex, this));
        }
    }

    @Override
    public MessageHandler messageHandler() {
        return this.xbeeMessageHandler;
    }

    @Override
    public MessageListener messageListener() {
        return this.xbeeMessageListener;
    }

    @Override
    public Optional<HttpHandler> httpHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<RegistrationHandler> registrationHandler() {
        return Optional.empty();
    }

    public XBeeDevice getxBeeDevice() {
        return xBeeDevice;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
}
