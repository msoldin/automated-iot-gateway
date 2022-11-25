package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageHandler;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.message.Header;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import de.hsbremen.iot.gateway.api.message.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AwsMqttMessageHandler implements MessageHandler {

    private final static Logger logger = LogManager.getLogger();

    private boolean running;

    private final Queue<Message> queue;

    private final AwsMqttAdapter awsMqttAdapter;

    private final AtomicInteger messageCount;

    public AwsMqttMessageHandler(AwsMqttAdapter awsMqttAdapter) {
        this.running = false;
        this.awsMqttAdapter = awsMqttAdapter;
        this.queue = new LinkedBlockingQueue<>();
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        new Thread(this).start();
        logger.info("AwsMqttMessageHandler successfully started!");
    }

    @Override
    public void stop() {
        this.running = false;
        synchronized (this) {
            this.notify();
        }
        logger.info("AwsMqttMessageHandler successfully shut down!");
    }

    @Override
    public void run() {
        try {
            this.running = true;
            MqttClientConnection mqttClient = this.awsMqttAdapter.getMqttClientConnection();
            MessageParser messageParser = this.awsMqttAdapter
                    .getServiceRegistry()
                    .messageService()
                    .getMessageParser();
            while (this.running) {
                this.sendMessages(mqttClient, messageParser);
                synchronized (this) {
                    if (this.queue.isEmpty()) {
                        this.wait();
                    }
                }
            }
        } catch (Exception ex) {
            this.awsMqttAdapter.getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceRuntimeException(ex, this.awsMqttAdapter));
        }
    }

    private void sendMessages(MqttClientConnection mqttClient, MessageParser messageParser) {
        while (!this.queue.isEmpty()) {
            try {
                final Message message = this.queue.poll();
                final Header header = message.getHeader();
                final QualityOfService qos = this.getQoS(header);
                if (this.awsMqttAdapter.getAwsMqttAdapterConfig().getMqttConnection().isDeviceShadowEnabled()) {
                    byte[] parsedMessage = messageParser.parseToLegacy(message);
                    MqttMessage mqttMessage = new MqttMessage(String.format("$aws/things/%s/shadow/update", header.getFrom()), parsedMessage);
                    mqttClient.publish(mqttMessage, qos, false);
                } else if (header.getTo() == null) {
                    byte[] parsedMessage = messageParser.parse(message);
                    MqttMessage mqttMessage = new MqttMessage("/clients", parsedMessage);
                    mqttClient.publish(mqttMessage, qos, false);
                } else {
                    byte[] parsedMessage = messageParser.parse(message);
                    MqttMessage mqttMessage = new MqttMessage(String.format("/clients/%s", header.getTo()), parsedMessage);
                    mqttClient.publish(mqttMessage, qos, false);
                }
                this.messageCount.incrementAndGet();
            } catch (Exception ex) {
                this.awsMqttAdapter.getServiceRegistry()
                        .exceptionService()
                        .handleException(ex);
            }
        }
    }

    private QualityOfService getQoS(Header header) {
        return header.getPriority() == Priority.BEST_EFFORT ? QualityOfService.AT_MOST_ONCE : QualityOfService.EXACTLY_ONCE;
    }

    @Override
    public void handle(Message message) {
        this.queue.offer(message);
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }

    @Override
    public int getSentQueueSize() {
        return this.queue.size();
    }

}
