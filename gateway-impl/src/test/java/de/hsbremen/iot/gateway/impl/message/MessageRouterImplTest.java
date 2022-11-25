package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.device.InternalDeviceService;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.Queue;

@TestInstance(Lifecycle.PER_CLASS)
public class MessageRouterImplTest {

    @Mock
    private InternalDeviceService deviceService;

    @Mock
    private InternalConfigService configService;
    @Mock
    private InternalServiceRegistry serviceRegistry;
    private MessageRouterImpl messageRouter;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.configService()).thenReturn(this.configService);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        Mockito.when(this.serviceRegistry.deviceService()).thenReturn(this.deviceService);
        this.messageRouter = new MessageRouterImpl(this.serviceRegistry);
    }

    @Test
    public void testHandle(){
        Queue<Message> messages = new LinkedList<>();
        messages.add(TestHelper.createRandomMessage());
        this.messageRouter.handle(messages);
    }

}
