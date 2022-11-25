package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class DeviceFactoryImplTest {

    private DeviceFactoryImpl deviceFactory;

    @BeforeAll
    public void start() {
        this.deviceFactory = new DeviceFactoryImpl();
    }

    @Test
    public void testCreateDevice() {
        String deviceId = "testClient1";
        int maxQos = 10;
        int currentQos = 10;
        boolean privileged = false;
        Device device = this.deviceFactory.createDevice(TestHelper.createConnectMessage(deviceId, maxQos, currentQos, privileged));
        Assertions.assertNotNull(device);
        Assertions.assertEquals(device.getDeviceId(), deviceId);
        Assertions.assertEquals(device.getCurrentQos(), maxQos);
        Assertions.assertEquals(device.getMaxQos(), currentQos);
        Assertions.assertFalse(device.isPrivileged());
    }

}
