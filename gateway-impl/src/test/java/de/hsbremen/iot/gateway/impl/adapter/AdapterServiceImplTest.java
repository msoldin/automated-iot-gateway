package de.hsbremen.iot.gateway.impl.adapter;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.adapter.InternalAdapterService;
import de.hsbremen.iot.gateway.api.http.HttpService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AdapterServiceImplTest {

    private static final String ADAPTER_ID = "mqtt-adapter";
    @Mock
    private Adapter adapter;

    @Mock
    private HttpService httpService;
    @Mock
    private InternalServiceRegistry serviceRegistry;

    private InternalAdapterService adapterService;
    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(adapter.id()).thenReturn(ADAPTER_ID);
        Mockito.when(this.serviceRegistry.httpService()).thenReturn(this.httpService);
        this.adapterService = new AdapterServiceImpl(this.serviceRegistry);
        this.adapterService.start();
    }

    @AfterAll
    public void stop() {
        this.adapterService.stop();
    }

    @Test
    @Order(1)
    public void testId() {
        this.adapterService.id();
    }

    @Test
    @Order(2)
    public void testRegisterAdapter() {
        this.adapterService.registerAdapter(this.adapter);
    }

    @Test
    @Order(3)
    public void testGetAdapter() {
        Assertions.assertEquals(this.adapterService.getAdapter(ADAPTER_ID).get(), this.adapter);
    }

    @Test
    @Order(4)
    public void testGetAdapters() {
        Assertions.assertFalse(this.adapterService.getAdapters().isEmpty());
    }

    @Test
    @Order(5)
    public void testRemoveAdapter() {
        this.adapterService.removeAdapter(adapter);
    }
}
