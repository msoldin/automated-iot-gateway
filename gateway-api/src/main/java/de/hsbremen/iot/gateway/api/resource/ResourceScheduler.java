package de.hsbremen.iot.gateway.api.resource;

public interface ResourceScheduler {

    void handle(ResourceUsageMeasurement measurement);

}
