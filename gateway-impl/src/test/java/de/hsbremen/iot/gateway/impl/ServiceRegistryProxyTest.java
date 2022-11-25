package de.hsbremen.iot.gateway.impl;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ServiceRegistryProxyTest {

    @Mock
    private InternalServiceRegistry serviceRegistry;

    private ServiceRegistryProxy serviceRegistryProxy;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.serviceRegistryProxy = new ServiceRegistryProxy(this.serviceRegistry);
    }

    @Test
    public void test(){
        this.serviceRegistryProxy.configService();
    }

}
