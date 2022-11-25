package de.hsbremen.iot.gateway.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ServiceRegistryImplTest {

    private ServiceRegistryImpl serviceRegistry;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.serviceRegistry = new ServiceRegistryImpl();
        this.serviceRegistry.start();
    }

    @AfterAll
    public void stop(){
        this.serviceRegistry.stop();
    }

    @Test
    public void testServiceRegistry(){
        this.serviceRegistry.id();
    }
}
