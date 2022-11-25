package de.hsbremen.iot.gateway.impl.device;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DeviceServiceImplTest {
//    private InternalServiceRegistry serviceRegistry;
//
//    private DeviceServiceImpl deviceService;
//
//    @BeforeAll
//    public void setup() {
//        this.serviceRegistry = new ServiceRegistryImpl();
//        this.deviceService = (DeviceServiceImpl) this.serviceRegistry.deviceService();
//        this.serviceRegistry.start();
//    }
//
//    @AfterAll
//    public void shutdown() {
//        this.serviceRegistry.stop();
//    }
//
//    @Test
//    public void testId() {
//        this.deviceService.id();
//    }
//
//    @Test
//    @Order(1)
//    public void testHandle() {
//        String deviceId = "testClient1";
//        int maxQos = 10;
//        int currentQos = 10;
//        boolean privileged = false;
//        this.deviceService.handle(DeviceMessageFactory.createConnectMessage(deviceId, maxQos, currentQos, privileged));
//    }
//
//    @Test
//    @Order(3)
//    public void testGetDevices() {
//        System.err.println(this.deviceService.getDevices());
//    }
//
//    @Test
//    @Order(2)
//    public void testGetDevice() {
//        Optional<Device> device = this.deviceService.getDevice("testClient1");
//        Assertions.assertFalse(device.isEmpty());
//        Assertions.assertNotNull(device.get());
//        Assertions.assertEquals(device.get().getDeviceId(), "testClient1");
//    }

}
