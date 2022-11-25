package de.hsbremen.iot.websocket.client;

import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.exception.MessageParserException;
import de.hsbremen.iot.client.api.message.Message;
import de.hsbremen.iot.client.impl.DebugMessageParser;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class WebSocketClientAdapter implements ClientAdapter {

    private final static Logger logger = LogManager.getLogger();

    private final Vertx vertx;

    private final HttpClient httpClient;

    private final DebugMessageParser messageParser;

    private final WebSocketClientAdapterConfig config;

    private WebSocket webSocket;

    private Consumer<Message> messageConsumer;

    public WebSocketClientAdapter(WebSocketClientAdapterConfig config, boolean lowBandwidth) {
        this.vertx = Vertx.vertx();
        this.httpClient = vertx.createHttpClient();
        this.messageParser = new DebugMessageParser(lowBandwidth);
        this.config = config;
    }

    @Override
    public void start() {
        WebSocketConnectOptions requestOptions = new WebSocketConnectOptions()
                .setHost(this.config.getHost())
                .setPort(this.config.getPort())
                .setURI(this.config.getUri())
                .setSsl(false);
        this.httpClient.webSocket(requestOptions, (ctx) -> {
            if (ctx.succeeded()) {
                this.webSocket = ctx.result();
                this.webSocket.handler(buffer -> {
                    try {
                        this.messageConsumer.accept(this.messageParser.parse(buffer.getBytes()));
                    } catch (MessageParserException e) {
                        logger.error(e);
                    }
                });
                this.webSocket.exceptionHandler(ex -> {
                    logger.error("Connection interrupted! {} {}", System.lineSeparator(), ExceptionUtils.getStackTrace(ex));
                    System.exit(-1);
                });
                this.webSocket.closeHandler(v -> {
                    logger.info("Connection closed to {}", requestOptions.toJson());
                    System.exit(-1);
                });
                synchronized (this) {
                    this.notify();
                }
            } else {
                logger.error("Failed to connect to {} {} {}", requestOptions.toJson(), System.lineSeparator(), ctx.cause());
                System.exit(-1);
            }
        });
        synchronized (this) {
            if (this.webSocket == null) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    logger.error("Connection interrupted! {} {}", System.lineSeparator(), ExceptionUtils.getStackTrace(ex));
                    System.exit(-1);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.webSocket.close();
        this.httpClient.close();
        this.vertx.close();
    }

    @Override
    public void send(Message message) {
        try {
            byte[] parsedMessage = this.messageParser.parse(message);
            this.webSocket.writeBinaryMessage(Buffer.buffer(parsedMessage)).result();
        } catch (MessageParserException e) {
            logger.error(e);
        }
    }

    @Override
    public void send(byte[] message) {
        this.webSocket.writeBinaryMessage(Buffer.buffer(message));
    }

    @Override
    public void onReceive(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

}
