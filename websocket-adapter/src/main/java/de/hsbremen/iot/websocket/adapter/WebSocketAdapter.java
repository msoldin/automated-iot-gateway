package de.hsbremen.iot.websocket.adapter;

import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.adapter.HttpHandler;
import de.hsbremen.iot.gateway.api.adapter.RegistrationHandler;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import io.vertx.core.http.ServerWebSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketAdapter implements Adapter {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "websocket-adapter";

    private final ServiceRegistry serviceRegistry;

    private final Map<String, ServerWebSocket> webSockets;

    private final WebSocketHttpHandler httpHandler;

    private final WebSocketMessageListener messageListener;

    private final WebSocketMessageHandler messageHandler;

    public WebSocketAdapter(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.webSockets = new ConcurrentHashMap<>();
        this.httpHandler = new WebSocketHttpHandler(this);
        this.messageHandler = new WebSocketMessageHandler(this);
        this.messageListener = new WebSocketMessageListener(this);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            this.messageListener.start();
            this.messageHandler.start();
            logger.info("WebSocketAdapter successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException(ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.messageListener.stop();
            this.messageHandler.stop();
            logger.info("WebSocketAdapter successfully shut down!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException(ex, this));
        }
    }

    @Override
    public WebSocketMessageHandler messageHandler() {
        return this.messageHandler;
    }

    @Override
    public WebSocketMessageListener messageListener() {
        return this.messageListener;
    }

    @Override
    public Optional<HttpHandler> httpHandler() {
        return Optional.of(this.httpHandler);
    }

    @Override
    public Optional<RegistrationHandler> registrationHandler() {
        return Optional.empty();
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public Map<String, ServerWebSocket> getWebSockets() {
        return this.webSockets;
    }
}
