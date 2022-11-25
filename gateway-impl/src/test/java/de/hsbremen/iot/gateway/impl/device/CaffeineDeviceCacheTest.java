package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.Device;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CaffeineDeviceCacheTest {

    @Mock
    private Device deviceOne;

    @Mock
    private Device deviceTwo;

    @Mock
    private Device deviceThree;

    private CaffeineDeviceCache caffeineDeviceCache;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.deviceOne.getDeviceId()).thenReturn("deviceOne");
        Mockito.when(this.deviceTwo.getDeviceId()).thenReturn("deviceTwo");
        Mockito.when(this.deviceThree.getDeviceId()).thenReturn("deviceThree");
        this.caffeineDeviceCache = new CaffeineDeviceCache();
    }


    @Test
    @Order(1)
    public void put() {
        this.caffeineDeviceCache.put(deviceOne);
    }

    @Test
    @Order(2)
    public void putAll() {
        this.caffeineDeviceCache.putAll(Arrays.asList(deviceTwo, deviceThree));
    }

    @Test
    @Order(3)
    public void get() {
        Assertions.assertNotNull(this.caffeineDeviceCache.get(deviceOne.getDeviceId()).get());
    }

}
