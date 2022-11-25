package de.hsbremen.iot.websocket.adapter;

import de.hsbremen.iot.gateway.api.adapter.MessageHandler;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketMessageHandler implements MessageHandler {

    private static final Logger logger = LogManager.getLogger();

    private boolean running;

    private final Queue<Message> queue;

    private final AtomicInteger messageCount;

    private final WebSocketAdapter webSocketAdapter;

    public WebSocketMessageHandler(WebSocketAdapter webSocketAdapter) {
        this.running = false;
        this.webSocketAdapter = webSocketAdapter;
        this.queue = new LinkedBlockingQueue<>();
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        new Thread(this).start();
        logger.info("WebSocketMessageHandler successfully started!");
    }

    @Override
    public void stop() {
        this.running = false;
        synchronized (this) {
            this.notify();
        }
        logger.info("WebSocketMessageHandler successfully shut down!");
    }

    @Override
    public int getSentQueueSize() {
        return this.queue.size();
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }

    @Override
    public void handle(Message message) {
        this.queue.offer(message);
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        try {
            this.running = true;
            MessageParser messageParser = this.webSocketAdapter
                    .getServiceRegistry()
                    .messageService()
                    .getMessageParser();
            while (this.running) {
                while (!this.queue.isEmpty()) {
                    try {
                        Message message = queue.poll();
                        String to = message.getHeader().getTo();
                        byte[] parsedMessage = messageParser.parse(message);
                        if (to == null) {
                            //broadcast
                            this.webSocketAdapter.getWebSockets()
                                    .values()
                                    .forEach(ws -> ws.writeBinaryMessage(Buffer.buffer(parsedMessage)));
                        } else {
                            //single routed message
                            ServerWebSocket ws = webSocketAdapter.getWebSockets().get(to);
                            if (ws != null) {
                                ws.writeBinaryMessage(Buffer.buffer(parsedMessage));
                            }
                        }
                        this.messageCount.incrementAndGet();
                    } catch (Exception ex) {
                        this.webSocketAdapter
                                .getServiceRegistry()
                                .exceptionService()
                                .handleException(ex);
                    }
                }
                synchronized (this) {
                    this.wait();
                }
            }
        } catch (Exception ex) {
            this.webSocketAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceRuntimeException(ex, this.webSocketAdapter));
        }
    }
}
