package de.hsbremen.iot.websocket.adapter;

import de.hsbremen.iot.gateway.api.adapter.HttpHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebSocketHttpHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger();

    private final WebSocketAdapter webSocketAdapter;

    public WebSocketHttpHandler(WebSocketAdapter webSocketAdapter) {
        this.webSocketAdapter = webSocketAdapter;
    }

    @Override
    public String route() {
        return "/websockets";
    }

    @Override
    public Handler<RoutingContext> handler() {
        return handler -> {
            HttpServerRequest request = handler.request();
            String deviceId = request.getParam("deviceId");
            if (deviceId == null) {
                handler.response()
                        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                        .setStatusMessage("Missing parameter 'deviceId'!")
                        .send();
                logger.debug("WebSocket-Connection request denied, missing 'deviceId'!");
            } else {
                Future<ServerWebSocket> future = handler.request().toWebSocket();
                future.onSuccess(ws -> {
                    ws.handler(buffer -> webSocketAdapter.messageListener().handleMessage(deviceId, buffer.getBytes()));
                    ws.exceptionHandler(ex -> {
                        ws.close();
                        logger.debug("WebSocket-Connection from {} closed on exception {} {}", deviceId, System.lineSeparator(), ExceptionUtils.getStackTrace(ex));
                    });
                    ws.closeHandler(s -> webSocketAdapter.getWebSockets().remove(deviceId));
                    this.webSocketAdapter.getWebSockets().put(deviceId, ws);
                    logger.debug("WebSocket-Connection request from {} accepted!", deviceId);
                });
                future.onFailure(ex -> {
                    logger.debug("WebSocket-Connection request from {} failed! {} {}", deviceId, System.lineSeparator(), ExceptionUtils.getStackTrace(ex));
                });
            }
        };
    }
}
