package de.hsbremen.iot.mqtt.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageListener;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.atomic.AtomicInteger;

public class MqttListener implements MessageListener {

    private final static Logger logger = LogManager.getLogger();

    private String topicFilter;

    private final MqttAdapter mqttAdapter;

    private final AtomicInteger messageCount;

    public MqttListener(MqttAdapter mqttAdapter) {
        this.mqttAdapter = mqttAdapter;
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        try {
            this.topicFilter = "/" + this.mqttAdapter.getServiceRegistry().configService().getConfig().getHostname() + "/#";
            MessageParser messageParser = this.mqttAdapter.getServiceRegistry().messageService().getMessageParser();
            MqttClient mqttClient = this.mqttAdapter.getMqttClient();
            mqttClient.subscribe(this.topicFilter, (topic, mqttMessage) -> {
                try {
                    byte[] payload = mqttMessage.getPayload();
                    String deviceId = topic.substring(topic.lastIndexOf("/"))
                            .replaceAll("/", "");
                    Message parsedMessage;
                    if (messageParser.isLegacy(payload)) {
                        parsedMessage = messageParser.parseLegacy(payload, this.mqttAdapter.id(), deviceId);
                    } else {
                        parsedMessage = messageParser.parse(payload, this.mqttAdapter.id(), deviceId);
                    }
                    this.mqttAdapter.getServiceRegistry().messageService().publish(parsedMessage);
                } catch (Exception ex) {
                    this.mqttAdapter.getServiceRegistry()
                            .exceptionService()
                            .handleException(ex);
                }
                this.messageCount.incrementAndGet();
            });
            logger.info("Subscribed on topic '{}'", topicFilter);
            logger.info("MqttListener successfully started!");
        } catch (Exception e) {
            this.mqttAdapter.getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceStartupException(e, mqttAdapter));
        }
    }

    @Override
    public void stop() {
        try {
            MqttClient mqttClient = mqttAdapter.getMqttClient();
            mqttClient.unsubscribe(topicFilter);
            logger.info("Unsubscribed on topic '{}'", topicFilter);
            logger.info("MqttListener successfully shut down!");
        } catch (Exception e) {
            this.mqttAdapter.getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceShutdownException(e, mqttAdapter));
        }
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }
}
