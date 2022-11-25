package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NitriteDeviceRepository implements DeviceRepository {

    private static final Logger logger = LogManager.getLogger();

    private static final String PATH = System.getProperty("user.home") +
            System.getProperty("file.separator") + ".gateway" +
            System.getProperty("file.separator") + "devices.db";

    private final Nitrite nitrite;
    private final ObjectRepository<Device> repository;

    public NitriteDeviceRepository() {
        this.nitrite = Nitrite.builder()
                .compressed()
                .filePath(PATH)
                .openOrCreate();
        this.repository = nitrite.getRepository(Device.class);
    }

    @Override
    public void save(Device device) {
        Objects.requireNonNull(device);
        logger.info("Saved device {} to local database", device);
        this.repository.update(this.createDefaultFilter(device.getDeviceId()), device, true);
    }

    @Override
    public List<Device> findAll() {
        return this.repository.find().toList();
    }

    @Override
    public List<Device> findNotPrivilegedDevices() {
        return this.repository.find(ObjectFilters.eq("privileged", false)).toList();
    }

    @Override
    public Optional<Device> findByDeviceId(String deviceId) {
        Objects.requireNonNull(deviceId);
        return Optional.ofNullable(this.repository.find(this.createDefaultFilter(deviceId))
                .firstOrDefault());
    }

    private ObjectFilter createDefaultFilter(String deviceId) {
        Objects.requireNonNull(deviceId);
        return ObjectFilters.eq("deviceId", deviceId);
    }

    @Override
    public void close() {
        this.repository.close();
        this.nitrite.close();
    }
}
