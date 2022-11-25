package de.hsbremen.iot.gateway.impl.device;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class CaffeineDeviceCache implements DeviceCache {

    private static final Logger logger = LogManager.getLogger();

    private final Cache<String, Device> cache;

    public CaffeineDeviceCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
        logger.info("Initialized Caffeine cache for devices [expireAfterWrite=10 minutes; maximumSize=1000]");
    }

    @Override
    public void put(Device device) {
        Objects.requireNonNull(device);
        if(logger.isTraceEnabled()){
            logger.trace("Caching Device {}!", device.getDeviceId());
        }
        this.cache.put(device.getDeviceId(), device);
    }

    @Override
    public void putAll(List<Device> devices) {
        Objects.requireNonNull(devices);
        for (Device device : devices) {
            this.put(device);
        }
    }

    @Override
    public Optional<Device> get(String deviceId) {
        Objects.requireNonNull(deviceId);
        if(logger.isTraceEnabled()){
            logger.trace("Resolving Device {} from cache!", deviceId);
        }
        return Optional.ofNullable(this.cache.getIfPresent(deviceId));
    }

}
