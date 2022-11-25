package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.message.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageServiceImpl implements InternalMessageService {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "MessageService";

    private boolean isRunning;

    private MessageRouter messageRouter;

    private MessageFilter messageFilter;

    private MessageParser messageParser;

    private MessageScheduler messageScheduler;

    private final InternalServiceRegistry serviceRegistry;

    private AtomicInteger sentMessageCount;

    private AtomicInteger receivedMessageCount;

    public MessageServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            this.sentMessageCount = new AtomicInteger(0);
            this.receivedMessageCount = new AtomicInteger(0);
            this.messageScheduler = new WFQMessageScheduler();
            this.messageFilter = new MessageFilterImpl(this.serviceRegistry);
            this.messageRouter = new MessageRouterImpl(this.serviceRegistry);
            this.messageParser = new MessageParserImpl(this.serviceRegistry);
            new Thread(this).start();
            logger.info("MessageService successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException("The MessageService could not be started!", ex, this));
        }

    }

    @Override
    public void stop() {
        try {
            this.isRunning = false;
            synchronized (this) {
                this.notify();
            }
            logger.info("MessageService successfully shut down!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The MessageService could not be shut down!", ex, this));
        }
    }

    @Override
    public void run() {
        this.isRunning = true;
        while (this.isRunning) {
            if (this.messageScheduler.isEmpty()) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        this.serviceRegistry
                                .exceptionService()
                                .handleException(new ServiceRuntimeException("MessageService interrupted", this));
                    }
                }
            }
            Queue<Message> batch = messageScheduler.schedule();
            int batchSize = batch.size();
            this.messageRouter.handle(batch);
            this.sentMessageCount.addAndGet(batchSize);
        }
    }

    @Override
    public void publish(Message message) {
        Objects.requireNonNull(message);
        if (this.serviceRegistry.configService()
                .getConfig()
                .getMessaging()
                .isMessageInterceptorEnabled()) {
            logger.info("Incoming message: {}", message);
        }
        if (!messageFilter.filter(message)) {
            this.messageScheduler.publish(message);
            this.receivedMessageCount.incrementAndGet();
            synchronized (this) {
                this.notify();
            }
        }
    }

    @Override
    public MessageParser getMessageParser() {
        return this.messageParser;
    }

    @Override
    public int getAndResetReceivedMessageCount() {
        return this.receivedMessageCount.getAndSet(0);
    }

    @Override
    public int getAndResetSentMessageCount() {
        return this.sentMessageCount.getAndSet(0);
    }

    @Override
    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    @Override
    public MessageFilter getMessageFilter() {
        return this.messageFilter;
    }

    @Override
    public MessageScheduler getMessageScheduler() {
        return this.messageScheduler;
    }

}
