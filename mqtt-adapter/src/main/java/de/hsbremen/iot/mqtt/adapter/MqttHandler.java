package de.hsbremen.iot.mqtt.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageHandler;
import de.hsbremen.iot.gateway.api.exception.MessageParserException;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import de.hsbremen.iot.gateway.api.message.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MqttHandler implements MessageHandler {

    private final static Logger logger = LogManager.getLogger();

    private boolean isRunning;

    private final Queue<Message> queue;

    private final MqttAdapter mqttAdapter;

    private final AtomicInteger messageCount;

    public MqttHandler(MqttAdapter mqttAdapter) {
        this.isRunning = false;
        this.mqttAdapter = mqttAdapter;
        this.queue = new LinkedBlockingQueue<>();
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        new Thread(this).start();
        logger.info("MqttHandler successfully started!");
    }

    @Override
    public void stop() {
        this.isRunning = false;
        synchronized (this) {
            this.notify();
        }
        logger.info("MqttHandler successfully shut down!");
    }

    @Override
    public int getSentQueueSize() {
        return this.queue.size();
    }

    @Override
    public void run() {
        try {
            this.isRunning = true;
            MqttClient mqttClient = this.mqttAdapter.getMqttClient();
            MessageParser messageParser = this.mqttAdapter
                    .getServiceRegistry()
                    .messageService()
                    .getMessageParser();
            while (this.isRunning) {
                while (!this.queue.isEmpty()) {
                    try {
                        Message message = queue.poll();
                        byte[] parsedMessage = messageParser.parse(message);
                        int qos = 0;
                        if (message.getHeader().getPriority() == Priority.HIGH_PRIORITY) {
                            qos = 2;
                        }
                        //check broadcast
                        if (message.getHeader().getTo() == null) {
                            mqttClient.publish("/clients", parsedMessage, qos, false);
                        } else {
                            mqttClient.publish("/clients/" + message.getHeader().getTo(), parsedMessage, qos, false);
                        }
                    } catch (MessageParserException ex) {
                        this.mqttAdapter.getServiceRegistry()
                                .exceptionService()
                                .handleException(ex);
                    }
                    this.messageCount.incrementAndGet();
                }
                synchronized (this) {
                    if (this.queue.isEmpty()) {
                        this.wait();
                    }
                }
            }
        } catch (Exception ex) {
            this.mqttAdapter.getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceRuntimeException(ex, mqttAdapter));
        }
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

}
