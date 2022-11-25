package de.hsbremen.iot.gateway.impl.resource;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.config.InternalConfigService;
import de.hsbremen.iot.gateway.api.message.InternalMessageService;
import de.hsbremen.iot.gateway.api.message.MessageFilter;
import de.hsbremen.iot.gateway.api.resource.MessageServiceUsage;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;
import de.hsbremen.iot.gateway.impl.utils.TestGatewayConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;

@TestInstance(Lifecycle.PER_CLASS)
public class ResourceSchedulerImplTest {

    @Mock
    private InternalConfigService configService;
    @Mock
    private MessageFilter messageFilter;
    @Mock
    private InternalMessageService messageService;
    @Mock
    private InternalServiceRegistry serviceRegistry;
    private ResourceSchedulerImpl resourceScheduler;

    @BeforeAll
    public void start() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(this.serviceRegistry.messageService()).thenReturn(this.messageService);
        Mockito.when(this.messageService.getMessageFilter()).thenReturn(this.messageFilter);
        Mockito.when(this.serviceRegistry.configService()).thenReturn(this.configService);
        Mockito.when(this.configService.getConfig()).thenReturn(new TestGatewayConfig());
        this.resourceScheduler = new ResourceSchedulerImpl(this.serviceRegistry);
    }

    @Test
    public void testHandle() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        ResourceUsageMeasurement resourceUsageMeasurement = ResourceUsageMeasurement.builder()
                .interval(1000)
                .systemTime(System.currentTimeMillis())
                .cpuUsageTotal(0.10)
                .cpuUsageProcess(0.10)
                .heapMemoryUsage(memoryMXBean.getHeapMemoryUsage())
                .nonHeapMemoryUsage(memoryMXBean.getNonHeapMemoryUsage())
                .adapterUsages(new ArrayList<>())
                .messageServiceUsage(new MessageServiceUsage(0, 0, 0))
                .build();
        this.resourceScheduler.handle(resourceUsageMeasurement);
    }


}
