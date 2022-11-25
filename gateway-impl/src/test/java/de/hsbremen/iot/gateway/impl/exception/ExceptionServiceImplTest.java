package de.hsbremen.iot.gateway.impl.exception;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
public class ExceptionServiceImplTest {

    @Mock
    private InternalServiceRegistry serviceRegistry;
    private ExceptionServiceImpl exceptionService;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.exceptionService = new ExceptionServiceImpl(this.serviceRegistry);
        this.exceptionService.start();
    }

    @AfterAll
    public void stop() {
        this.exceptionService.stop();
    }

    @Test
    public void testId() {
        this.exceptionService.id();
    }

    @Test
    public void testHandleException() {
        this.exceptionService.handleException(new RuntimeException());
    }

}
