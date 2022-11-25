package de.hsbremen.iot.gateway.impl.resource;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.InternalAdapterService;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.message.InternalMessageService;
import de.hsbremen.iot.gateway.api.message.MessageScheduler;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

@TestInstance(Lifecycle.PER_CLASS)
public class ResourceActuatorImplTest {

    @Mock
    private InternalConfigService configService;
    @Mock
    private MessageScheduler messageScheduler;
    @Mock
    private InternalAdapterService adapterService;

    @Mock
    private InternalMessageService messageService;
    @Mock
    private InternalServiceRegistry serviceRegistry;
    private ResourceActuatorImpl resourceActuator;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.adapterService()).thenReturn(this.adapterService);
        Mockito.when(this.adapterService.getAdapters()).thenReturn(new HashMap<>());
        Mockito.when(this.serviceRegistry.messageService()).thenReturn(this.messageService);
        Mockito.when(this.messageService.getAndResetReceivedMessageCount()).thenReturn(0);
        Mockito.when(this.messageService.getAndResetSentMessageCount()).thenReturn(0);
        Mockito.when(this.messageService.getMessageScheduler()).thenReturn(this.messageScheduler);
        Mockito.when(this.messageScheduler.getSize()).thenReturn(0);
        Mockito.when(this.serviceRegistry.configService()).thenReturn(this.configService);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        this.resourceActuator = new ResourceActuatorImpl(this.serviceRegistry);
    }

    @Test
    public void testGetResourceState() {
        ResourceUsageMeasurement resourceUsageMeasurement = this.resourceActuator.getResourceState();
        Assertions.assertNotNull(resourceUsageMeasurement);
    }

}
