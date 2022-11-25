package de.hsbremen.iot.gateway.impl.resource;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.resource.ResourceActuator;
import de.hsbremen.iot.gateway.api.resource.ResourceScheduler;
import de.hsbremen.iot.gateway.api.resource.ResourceService;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LogManager.getLogger();

    protected static final String ID = "ResourceService";

    private ScheduledExecutorService executor;

    private ResourceActuator resourceActuator;

    private ResourceScheduler resourceScheduler;

    private ResourceServiceHttpHandler httpHandler;

    private final InternalServiceRegistry serviceRegistry;

    public ResourceServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            long interval = this.serviceRegistry.configService().getConfig().getMonitoring().getInterval();
            this.httpHandler = new ResourceServiceHttpHandler();
            this.resourceActuator = new ResourceActuatorImpl(this.serviceRegistry);
            this.resourceScheduler = new ResourceSchedulerImpl(this.serviceRegistry);
            this.serviceRegistry.httpService().registerHttpHandler(this.httpHandler);
            this.executor = Executors.newSingleThreadScheduledExecutor();
            this.executor.scheduleAtFixedRate(this, 0, interval, TimeUnit.MILLISECONDS);
            logger.info("ResourceService successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException("The ResourceService could not be started!", ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.serviceRegistry.httpService().removeHttpHandler(this.httpHandler);
            this.executor.shutdown();
            this.executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            logger.info("ResourceService successfully shut down!");
        } catch (Exception e) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The ResourceService could not be shut down!", e, this));
        }
    }

    @Override
    public void run() {
        ResourceUsageMeasurement resourceUsageMeasurement = this.resourceActuator.getResourceState();
        this.httpHandler.setState(resourceUsageMeasurement);
        this.resourceScheduler.handle(resourceUsageMeasurement);
    }

}
