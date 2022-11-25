package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.*;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.Optional;

public class AwsMqttAdapter implements Adapter {

    private final static Logger logger = LogManager.getLogger();

    private final static String ID = "aws-mqtt-adapter";

    private final ServiceRegistry serviceRegistry;

    private AwsMqttAdapterConfig awsMqttAdapterConfig;

    private final AwsMqttMessageHandler messageHandler;

    private final AwsMqttMessageListener messageListener;

    private final AwsRegistrationHandler registrationHandler;

    private MqttClientConnection mqttClientConnection;

    public AwsMqttAdapter(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.messageListener = new AwsMqttMessageListener(this);
        this.messageHandler = new AwsMqttMessageHandler(this);
        this.registrationHandler = new AwsRegistrationHandler(this);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            this.awsMqttAdapterConfig = this.serviceRegistry.configService()
                    .getConfigFromBase(ID, AwsMqttAdapterConfig.class);
            AwsMqttClientConfig awsMqttConfig = this.awsMqttAdapterConfig.getMqttConnection();
            EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            HostResolver resolver = new HostResolver(eventLoopGroup);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
            this.mqttClientConnection = AwsIotMqttConnectionBuilder
                    .newMtlsBuilderFromPath(awsMqttConfig.getCertPath(), awsMqttConfig.getKeyPath())
                    .withClientId(awsMqttConfig.getClientId())
                    .withCleanSession(true)
                    .withEndpoint(awsMqttConfig.getEndpoint())
                    .withBootstrap(clientBootstrap)
                    .build();
            this.mqttClientConnection.connect().get();
            this.registrationHandler.start();
            this.messageHandler.start();
            this.messageListener.start();
            logger.info("AwsMqttAdapter successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry.exceptionService()
                    .handleException(new ServiceStartupException("The AwsMqttAdapter could not be started! ", ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.registrationHandler.stop();
            this.messageHandler.stop();
            this.messageListener.stop();
            this.mqttClientConnection.disconnect().get();
            logger.info("AwsMqttAdapter successfully shut down!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The AwsMqttAdapter could not be shut down!", ex, this));
        }
    }

    @Override
    public MessageHandler messageHandler() {
        return this.messageHandler;
    }

    @Override
    public MessageListener messageListener() {
        return this.messageListener;
    }

    @Override
    public Optional<HttpHandler> httpHandler() {
        return Optional.empty();
    }

    @Override
    public Optional<RegistrationHandler> registrationHandler() {
        return Optional.of(this.registrationHandler);
    }

    public MqttClientConnection getMqttClientConnection() {
        return this.mqttClientConnection;
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    public AwsMqttAdapterConfig getAwsMqttAdapterConfig() {
        return this.awsMqttAdapterConfig;
    }

}
