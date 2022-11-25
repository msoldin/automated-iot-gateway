package de.hsbremen.iot.gateway.impl.exception;

import de.hsbremen.iot.gateway.api.exception.ExceptionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ExceptionServiceProxyTest {

    @Mock
    private ExceptionService exceptionService;

    private ExceptionServiceProxy exceptionServiceProxy;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.exceptionServiceProxy = new ExceptionServiceProxy(this.exceptionService);
    }

    @Test
    public void testHandleException() {
        this.exceptionServiceProxy.handleException(new RuntimeException());
    }


}
