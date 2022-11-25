package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.device.InternalDeviceService;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@TestInstance(Lifecycle.PER_CLASS)
public class MessageFilterImplTest {

    @Mock
    private InternalDeviceService deviceService;
    @Mock
    private InternalServiceRegistry serviceRegistry;

    private MessageFilterImpl messageFilter;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.deviceService()).thenReturn(this.deviceService);
        Mockito.when(this.deviceService.getDevice(any())).thenReturn(Optional.of(TestHelper.createDevice()));
        this.messageFilter = new MessageFilterImpl(serviceRegistry);
    }

    @Test
    public void testFilter() {
        Message message = TestHelper.createRandomMessage();
        this.messageFilter.filter(message);
    }


}
