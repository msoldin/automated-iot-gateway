package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageListener;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import java.util.concurrent.atomic.AtomicInteger;

public class AwsMqttMessageListener implements MessageListener {

    private final static Logger logger = LogManager.getLogger();

    private String topicFilter;

    private final AtomicInteger messageCount;

    private final AwsMqttAdapter awsMqttAdapter;

    public AwsMqttMessageListener(AwsMqttAdapter adapter) {
        this.awsMqttAdapter = adapter;
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        this.topicFilter = "/" + this.awsMqttAdapter.getServiceRegistry()
                .configService()
                .getConfig()
                .getHostname() + "/#";
        MessageParser messageParser = awsMqttAdapter.getServiceRegistry().messageService().getMessageParser();
        this.awsMqttAdapter.getMqttClientConnection().subscribe(this.topicFilter, QualityOfService.AT_LEAST_ONCE, mqttMessage -> {
            try {
                String topic = mqttMessage.getTopic();
                byte[] payload = mqttMessage.getPayload();
                String deviceId = topic.substring(topic.lastIndexOf("/"))
                        .replaceAll("/", "");
                Message parsedMessage;
                if (messageParser.isLegacy(payload)) {
                    parsedMessage = messageParser.parseLegacy(payload, this.awsMqttAdapter.id(), deviceId);
                } else {
                    parsedMessage = messageParser.parse(payload, this.awsMqttAdapter.id(), deviceId);
                }
                this.awsMqttAdapter.getServiceRegistry().messageService().publish(parsedMessage);
            } catch (Exception ex) {
                this.awsMqttAdapter.getServiceRegistry()
                        .exceptionService()
                        .handleException(ex);
            }
            this.messageCount.incrementAndGet();
        });
        logger.info("Subscribed on topic '{}'", topicFilter);
        logger.info("AwsMqttMessageListener successfully started!");
    }

    @Override
    public void stop() {
        try {
            MqttClientConnection mqttClientConnection = this.awsMqttAdapter.getMqttClientConnection();
            mqttClientConnection.unsubscribe(this.topicFilter);
            logger.info("Unsubscribed on topic '{}'", topicFilter);
            logger.info("AwsMqttMessageListener successfully shut down!");
        } catch (Exception ex) {
            this.awsMqttAdapter.getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceShutdownException(ex, this.awsMqttAdapter));
        }
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }
}
