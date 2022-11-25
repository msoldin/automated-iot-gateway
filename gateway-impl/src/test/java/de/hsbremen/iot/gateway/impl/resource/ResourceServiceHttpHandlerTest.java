package de.hsbremen.iot.gateway.impl.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ResourceServiceHttpHandlerTest {

    private ResourceServiceHttpHandler resourceServiceHttpHandler;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.resourceServiceHttpHandler = new ResourceServiceHttpHandler();
    }

    @Test
    public void testRoute() {
        Assertions.assertEquals(this.resourceServiceHttpHandler.route(), "/gateway/resources");
    }

    @Test
    public void testHandler(){
        Assertions.assertNotNull(this.resourceServiceHttpHandler.handler());
    }

    @Test
    public void testGetState(){
        Assertions.assertNull(this.resourceServiceHttpHandler.getState());
    }



}
