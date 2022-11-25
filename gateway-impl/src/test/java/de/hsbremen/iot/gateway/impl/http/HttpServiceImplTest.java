package de.hsbremen.iot.gateway.impl.http;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.HttpHandler;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.exception.InternalExceptionService;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class HttpServiceImplTest {

    @Mock
    private InternalExceptionService exceptionService;
    @Mock
    private InternalConfigService configService;
    @Mock
    private InternalServiceRegistry serviceRegistry;

    private HttpServiceImpl httpService;

    private HttpHandler httpHandler;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.configService()).thenReturn(this.configService);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        Mockito.when(this.serviceRegistry.exceptionService()).thenReturn(this.exceptionService);
        this.httpService = new HttpServiceImpl(this.serviceRegistry);
        this.httpService.start();

        this.httpHandler = new HttpHandler() {
            @Override
            public String route() {
                return "/test";
            }

            @Override
            public Handler<RoutingContext> handler() {
                return ctx -> {
                    HttpServerResponse response = ctx.response();
                    response.putHeader("content-type", "application/json");
                    response.end("Test");
                };
            }
        };
    }

    @AfterAll
    public void stop() {
        this.httpService.stop();
    }

    @Test
    public void testRegisterHttpHandler() {
        this.httpService.registerHttpHandler(this.httpHandler);
    }

    @Test
    public void testRemoveHttpHandler() {
        this.httpService.removeHttpHandler(this.httpHandler);
    }
}
