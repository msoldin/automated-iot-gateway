package de.hsbremen.iot.gateway.impl.http;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.HttpHandler;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.http.HttpService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class HttpServiceImpl implements HttpService {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "HttpService";

    private final InternalServiceRegistry serviceRegistry;

    private Vertx vertx;

    private Router router;

    private HttpServer httpServer;

    public HttpServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            int port = this.serviceRegistry.configService().getConfig().getVertx().getPort();
            this.vertx = Vertx.vertx();
            this.router = Router.router(vertx);
            this.httpServer = vertx.createHttpServer()
                    .requestHandler(this.router)
                    .listen(port)
                    .result();
            logger.info("HttpService successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException("The HttpService could not be started!", ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.router.clear();
            this.httpServer.close();
            this.vertx.close();
            logger.info("HttpService successfully shut down!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The HttpService could not be shut down!", ex, this));
        }
    }

    @Override
    public void registerHttpHandler(HttpHandler httpHandler) {
        Objects.requireNonNull(httpHandler);
        router.get(httpHandler.route()).handler(httpHandler.handler());
    }

    @Override
    public void removeHttpHandler(HttpHandler httpHandler) {
        Objects.requireNonNull(httpHandler);
        router.getRoutes().stream()
                .filter(route -> route.getPath().equals(httpHandler.route()))
                .forEach(Route::remove);
    }

}
