package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;
import de.hsbremen.iot.gateway.api.message.MessageService;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class MessageServiceProxyTest {

    @Mock
    private MessageParser messageParser;
    @Mock
    private MessageService messageService;

    private MessageServiceProxy messageServiceProxy;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.messageService.getMessageParser()).thenReturn(this.messageParser);
        this.messageServiceProxy = new MessageServiceProxy(this.messageService);
    }

    @Test
    public void testPublish() {
        Message message = TestHelper.createRandomMessage();
        this.messageServiceProxy.publish(message);
    }

    @Test
    public void testGetMessageParser() {
        Assertions.assertNotNull(this.messageServiceProxy.getMessageParser());
    }


}
