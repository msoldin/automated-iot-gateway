package de.hsbremen.iot.gateway.impl.adapter;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.adapter.InternalAdapterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AdapterServiceImpl implements InternalAdapterService {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "AdapterService";

    private final Map<String, Adapter> adapters;

    private final InternalServiceRegistry serviceRegistry;

    public AdapterServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.adapters = new HashMap<>();
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        logger.info("AdapterService successfully started!");
    }

    @Override
    public void stop() {
        logger.info("AdapterService successfully shut down!");
    }

    @Override
    public Optional<Adapter> getAdapter(String adapterId) {
        return Optional.ofNullable(this.adapters.get(adapterId));
    }

    @Override
    public Map<String, Adapter> getAdapters() {
        return this.adapters;
    }

    @Override
    public void registerAdapter(Adapter adapter) {
        Objects.requireNonNull(adapter);
        Objects.requireNonNull(adapter.id());
        this.adapters.put(adapter.id(), adapter);
        adapter.httpHandler().ifPresent(serviceRegistry.httpService()::registerHttpHandler);
        adapter.start();
        logger.info("Registered Adapter {} in AdapterService", adapter.id());
    }

    @Override
    public void removeAdapter(Adapter adapter) {
        Objects.requireNonNull(adapter);
        this.adapters.remove(adapter.id());
        adapter.httpHandler().ifPresent(serviceRegistry.httpService()::removeHttpHandler);
        adapter.stop();
        logger.info("Removed Adapter {} from AdapterService", adapter.id());
    }

}
