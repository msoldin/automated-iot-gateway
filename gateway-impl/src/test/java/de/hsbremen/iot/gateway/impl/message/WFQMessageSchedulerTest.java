package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.Priority;
import de.hsbremen.iot.gateway.impl.utils.TestHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockitoAnnotations;

import java.util.Queue;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class WFQMessageSchedulerTest {

    private WFQMessageScheduler messageScheduler;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        this.messageScheduler = new WFQMessageScheduler();
    }

    @Test
    @Order(1)
    public void testPublish() {
        for (int i = 0; i < 10; i++) {
            this.messageScheduler.publish(TestHelper.createRandomMessage());
        }
        this.messageScheduler.publish(TestHelper.createConnectMessage(TestHelper.FROM, 10, 10, true));
        Assertions.assertFalse(this.messageScheduler.isEmpty());
        Assertions.assertEquals(this.messageScheduler.getSize(), 11);
    }

    @Test
    @Order(2)
    public void testSchedule() {
        Queue<Message> messages = this.messageScheduler.schedule();
        Assertions.assertEquals(messages.size(), 11);
        Assertions.assertEquals(messages.remove().getHeader().getPriority(), Priority.HIGH_PRIORITY);
    }

}
