package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.DeviceConnectPayload;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class DevicePayloadParserTest {

    private DevicePayloadParser devicePayloadParser;

    @BeforeAll
    public void start() {
        this.devicePayloadParser = new DevicePayloadParser();
    }

    @Test
    public void testParse() {
        String deviceId = "testClient1";
        int maxQos = 10;
        int currentQos = 10;
        boolean privileged = false;
        Message message = TestHelper.createConnectMessage(deviceId, maxQos, currentQos, privileged);
        DeviceConnectPayload payload = this.devicePayloadParser.parse(message, DeviceConnectPayload.class);
        Assertions.assertNotNull(payload);
        Assertions.assertEquals(payload.getMaxQos(), maxQos);
        Assertions.assertEquals(payload.getCurrentQos(), currentQos);
        Assertions.assertFalse(payload.isPrivileged());
    }

}
