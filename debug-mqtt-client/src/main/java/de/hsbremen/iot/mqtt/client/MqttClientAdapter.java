package de.hsbremen.iot.mqtt.client;

import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.exception.MessageParserException;
import de.hsbremen.iot.client.api.message.Message;
import de.hsbremen.iot.client.api.message.Priority;
import de.hsbremen.iot.client.impl.DebugMessageParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.function.Consumer;

public class MqttClientAdapter implements ClientAdapter {

    private final static Logger logger = LogManager.getLogger();

    private final MqttClient mqttClient;

    private final MqttClientAdapterConfig config;

    private final DebugMessageParser messageParser;

    private Consumer<Message> messageConsumer;

    public MqttClientAdapter(MqttClientAdapterConfig config, boolean lowBandwidth) throws MqttException {
        this.config = config;
        this.messageParser = new DebugMessageParser(lowBandwidth);
        this.mqttClient = new MqttClient(config.getServerUri(), config.getClientId(), new MemoryPersistence());
    }

    @Override
    public void send(Message message) {
        try {
            byte[] parsedMessage = this.messageParser.parse(message);
            String topic = "/" + this.config.getGatewayHostname() + "/" + this.config.getClientId();
            int qos = message.getHeader().getPriority() == Priority.HIGH_PRIORITY ? 2 : 0;
            this.mqttClient.publish(topic, parsedMessage, qos, false);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void send(byte[] message) {
        try {
            String topic = "/" + this.config.getGatewayHostname() + "/" + this.config.getClientId();
            this.mqttClient.publish(topic, message, 0, false);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onReceive(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void start() {
        try {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttClient.connect(mqttConnectOptions);
            mqttClient.subscribe("/clients/" + this.config.getClientId(), (s, mqttMessage) -> {
                try {
                    this.messageConsumer.accept(this.messageParser.parse(mqttMessage.getPayload()));
                } catch (MessageParserException ex) {
                    logger.error(ex);
                }
            });
            mqttClient.subscribe("/clients", (s, mqttMessage) -> {
                try {
                    this.messageConsumer.accept(this.messageParser.parse(mqttMessage.getPayload()));
                } catch (MessageParserException ex) {
                    logger.error(ex);
                }
            });
        } catch (MqttException e) {
            logger.error(e);
        }
    }

    @Override
    public void stop() {
        try {
            this.mqttClient.close();
        } catch (MqttException e) {
            logger.error(e);
        }
    }
}
