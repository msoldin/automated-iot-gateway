package de.hsbremen.iot.websocket.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageListener;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketMessageListener implements MessageListener {

    private final static Logger logger = LogManager.getLogger();

    private final AtomicInteger messageCount;

    private final WebSocketAdapter webSocketAdapter;

    public WebSocketMessageListener(WebSocketAdapter webSocketAdapter) {
        this.webSocketAdapter = webSocketAdapter;
        this.messageCount = new AtomicInteger(0);
    }


    @Override
    public void start() {
        logger.info("WebSocketMessageListener successfully started!");
    }

    @Override
    public void stop() {
        logger.info("WebSocketMessageListener successfully shut down!");
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }

    public void handleMessage(String deviceId, byte[] message) {
        try {
            MessageParser messageParser = webSocketAdapter.getServiceRegistry()
                    .messageService()
                    .getMessageParser();
            Message parsedMessage;
            if (messageParser.isLegacy(message)) {
                parsedMessage = messageParser.parseLegacy(message, webSocketAdapter.id(), deviceId);
            } else {
                parsedMessage = messageParser.parse(message, webSocketAdapter.id(), deviceId);
            }
            this.webSocketAdapter.getServiceRegistry()
                    .messageService()
                    .publish(parsedMessage);
            this.messageCount.incrementAndGet();
        } catch (Exception ex) {
            this.webSocketAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(ex);
        }
    }
}
