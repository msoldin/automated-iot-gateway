package de.hsbremen.iot.gateway.impl.config;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.GatewayConfig;
import de.hsbremen.iot.gateway.api.config.MailConfig;
import de.hsbremen.iot.gateway.api.exception.ConfigException;
import de.hsbremen.iot.gateway.api.exception.InternalExceptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ConfigServiceImplTest {

    @TempDir
    File tempDir;

    @Mock
    InternalServiceRegistry serviceRegistry;

    @Mock
    InternalExceptionService exceptionService;

    ConfigServiceImpl configService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.exceptionService()).thenReturn(this.exceptionService);
        this.configService = new ConfigServiceImpl(this.serviceRegistry,
                URLDecoder.decode(new File(this.getClass().getResource("/config.yaml").getFile()).getPath(), StandardCharsets.UTF_8));
    }

    @Test
    public void testId() {
        this.configService.id();
    }

    @Test
    public void testBaseConstructor(){
        ConfigServiceImpl configService = new ConfigServiceImpl(this.serviceRegistry);
    }

    @Test
    public void testMissingConfigurationFile() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ConfigServiceImpl configService = new ConfigServiceImpl(this.serviceRegistry, tempDir.getPath());
            configService.start();
            configService.getConfig(GatewayConfig.class);
        });
    }

    @Test
    public void testMissingConfigurationFolder() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ConfigServiceImpl configService = new ConfigServiceImpl(this.serviceRegistry,
                    tempDir.getPath() + File.pathSeparatorChar);
            configService.start();
            configService.getConfig(GatewayConfig.class);
        });
    }

    @Test
    public void testInvalidConfiguration() {
        Assertions.assertThrows(ConfigException.class, () -> {
            ConfigServiceImpl configService = new ConfigServiceImpl(this.serviceRegistry,
                    URLDecoder.decode(new File(this.getClass().getResource("/empty_config.yaml").getFile()).getPath(), StandardCharsets.UTF_8));
            configService.start();
            configService.getConfig(GatewayConfig.class);
        });
    }

    @Test
    public void testGetConfig(){
        this.configService.start();
        Assertions.assertNotNull(this.configService.getConfig());
        this.configService.stop();
    }

    @Test
    public void testGetConfigAsClass() throws ConfigException {
        this.configService.start();
        Assertions.assertNotNull(this.configService.getConfig( GatewayConfig.class));
        this.configService.stop();
    }

    @Test
    public void testGetConfigFromBase() throws ConfigException {
        this.configService.start();
        Assertions.assertNotNull(this.configService.getConfigFromBase("mail", MailConfig.class));
        this.configService.stop();
    }

}

