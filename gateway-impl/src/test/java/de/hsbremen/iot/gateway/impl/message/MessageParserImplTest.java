package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.device.InternalDeviceService;
import de.hsbremen.iot.gateway.api.exception.MessageParserException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@TestInstance(Lifecycle.PER_CLASS)
public class MessageParserImplTest {

    @Mock
    private InternalDeviceService deviceService;
    @Mock
    private InternalConfigService configService;
    @Mock
    private InternalServiceRegistry serviceRegistry;
    private MessageParserImpl messageParser;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.configService()).thenReturn(this.configService);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        Mockito.when(this.serviceRegistry.deviceService()).thenReturn(this.deviceService);
        Mockito.when(this.deviceService.getDevice(any())).thenReturn(Optional.of(TestHelper.createDevice()));
        this.messageParser = new MessageParserImpl(this.serviceRegistry);
    }

    @Test
    public void testParse() throws MessageParserException {
        Message message = TestHelper.createRandomMessage();
        byte[] outgoingMessage = this.messageParser.parse(message);
        Assertions.assertNotNull(outgoingMessage);
        Message incomingMessage = this.messageParser.parse(outgoingMessage, TestHelper.FROM_SERVICE);
        Assertions.assertEquals(message, incomingMessage);
    }

    @Test
    public void testParseLegacy() throws MessageParserException {
        byte[] message = TestHelper.createRandomPayload(255);
        Message incomingMessage = this.messageParser.parseLegacy(message, TestHelper.FROM_SERVICE, TestHelper.FROM);
        Assertions.assertNotNull(incomingMessage);
        byte[] outgoingMessage = this.messageParser.parseToLegacy(incomingMessage);
        Assertions.assertEquals(message, outgoingMessage);
    }

    @Test
    public void testParseForLowBandwidth() throws MessageParserException {
        Message message = TestHelper.createRandomMessage();
        byte[] outgoingMessageHigh = this.messageParser.parse(message);
        byte[] outgoingMessageLow = this.messageParser.parseForLowBandwidth(message);
        Assertions.assertNotEquals(outgoingMessageLow, outgoingMessageHigh);
        Message incomingMessage = this.messageParser.parse(outgoingMessageLow, TestHelper.FROM_SERVICE, TestHelper.FROM);
        Assertions.assertEquals(incomingMessage, message);
    }

    @Test
    public void testIsLegacy() throws MessageParserException {
        byte[] message = TestHelper.createRandomPayload(255);
        boolean isLegacy = this.messageParser.isLegacy(message);
        Assertions.assertTrue(isLegacy);
    }
}
