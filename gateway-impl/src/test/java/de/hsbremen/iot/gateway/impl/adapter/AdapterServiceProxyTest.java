package de.hsbremen.iot.gateway.impl.adapter;

import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.adapter.AdapterService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AdapterServiceProxyTest {

    private static final String ADAPTER_ID = "mqtt-adapter";

    @Mock
    private Adapter adapter;
    @Mock
    private AdapterService adapterService;
    private AdapterServiceProxy adapterServiceProxy;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.adapterServiceProxy = new AdapterServiceProxy(this.adapterService);
    }

    @Test
    @Order(1)
    public void testRegisterAdapter() {
        this.adapterServiceProxy.registerAdapter(this.adapter);
    }

    @Test
    @Order(2)
    public void testRemoveAdapter() {
        this.adapterServiceProxy.removeAdapter(this.adapter);
    }

}
