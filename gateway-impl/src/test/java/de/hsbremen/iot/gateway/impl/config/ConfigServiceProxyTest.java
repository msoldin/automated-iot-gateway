package de.hsbremen.iot.gateway.impl.config;

import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.config.GatewayConfig;
import de.hsbremen.iot.gateway.api.config.MailConfig;
import de.hsbremen.iot.gateway.api.exception.ConfigException;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import de.hsbremen.iot.gateway.impl.utils.TestMailConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ConfigServiceProxyTest {

    @Mock
    private ConfigService configService;

    private ConfigServiceProxy configServiceProxy;

    @BeforeAll
    public void start() throws ConfigException {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        Mockito.when(this.configService.getConfig(GatewayConfig.class)).thenReturn(new TestGatewayConfig());
        Mockito.when(this.configService.getConfigFromBase("mail", MailConfig.class)).thenReturn(new TestMailConfig());
        this.configServiceProxy = new ConfigServiceProxy(this.configService);
    }

    @Test
    public void testGetConfig() {
        Assertions.assertNotNull(this.configServiceProxy.getConfig());
    }

    @Test
    public void testGetConfigAsClass() throws ConfigException {
        Assertions.assertNotNull(this.configServiceProxy.getConfig(GatewayConfig.class));
    }

    @Test
    public void testGetConfigFromBase() throws ConfigException {
        Assertions.assertNotNull(this.configServiceProxy.getConfigFromBase("mail", MailConfig.class));
    }

}
