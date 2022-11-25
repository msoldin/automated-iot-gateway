package de.hsbremen.iot.mqtt.adapter;

import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.*;
import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Optional;

public class MqttAdapter implements Adapter {

    private final static Logger logger = LogManager.getLogger();

    private final static String ID = "mqtt-adapter";

    private final ServiceRegistry serviceRegistry;

    private MqttClient mqttClient;

    private final MqttHandler mqttHandler;

    private final MqttListener mqttListener;

    public MqttAdapter(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.mqttHandler = new MqttHandler(this);
        this.mqttListener = new MqttListener(this);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            ConfigService configService = this.serviceRegistry.configService();
            String hostname = configService.getConfig().getHostname();
            MqttAdapterConfig config = configService.getConfigFromBase(ID, MqttAdapterConfig.class);
            this.mqttClient = new MqttClient(config.getServerUri(), hostname, new MemoryPersistence());
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(config.isCleanSession());
            mqttConnectOptions.setAutomaticReconnect(config.isAutomaticReconnect());
            mqttConnectOptions.setKeepAliveInterval(10);
            mqttClient.connect(mqttConnectOptions);
            this.mqttListener.start();
            this.mqttHandler.start();
            logger.info("MqttAdapter successfully started!");
        } catch (Exception e) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException(e, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.mqttHandler.stop();
            this.mqttListener.stop();
            this.mqttClient.disconnect();
        } catch (MqttException e) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException(e, this));
        }
        logger.info("MqttAdapter successfully shut down!");
    }

    @Override
    public MessageHandler messageHandler() {
        return this.mqttHandler;
    }

    @Override
    public MessageListener messageListener() {
        return this.mqttListener;
    }

    @Override
    public Optional<HttpHandler> httpHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<RegistrationHandler> registrationHandler() {
        return Optional.empty();
    }

    public MqttClient getMqttClient() {
        return this.mqttClient;
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }
}
