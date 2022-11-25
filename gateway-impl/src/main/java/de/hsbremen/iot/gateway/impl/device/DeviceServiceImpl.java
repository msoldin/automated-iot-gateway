package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.device.*;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.message.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DeviceServiceImpl implements InternalDeviceService {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "DeviceService";

    private final InternalServiceRegistry serviceRegistry;

    private DeviceCache cache;

    private DeviceFactory factory;

    private PayloadParser payloadParser;

    private DeviceRepository deviceRepository;

    public DeviceServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;

    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            this.cache = new CaffeineDeviceCache();
            this.factory = new DeviceFactoryImpl();
            this.payloadParser = new DevicePayloadParser();
            this.deviceRepository = new NitriteDeviceRepository();
            logger.info("DeviceService successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException("The DeviceService could not be started!", ex, this));
        }
    }

    @Override
    public void stop() {
        try {
            this.deviceRepository.close();
            logger.info("DeviceService successfully shut down!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The DeviceService could not be shut down!", ex, this));
        }
    }

    @Override
    public void handle(Message message) {
        CompletableFuture.runAsync(() -> {
            try {
                Header header = message.getHeader();
                switch (header.getType()) {
                    case CONNECT:
                        this.connectDevice(message);
                        break;
                    case DISCONNECT:
                        this.disconnectDevice(message);
                        break;
                    case LEGACY_CONNECT:
                        this.connectLegacyDevice(message);
                        break;
                    case QOS_CHANGED:
                        this.changeQos(message);
                        break;
                }
            } catch (Exception ex) {
                this.serviceRegistry.exceptionService()
                        .handleException(ex);
            }
        });
    }

    @Override
    public Optional<Device> getDevice(String deviceId) {
        Objects.requireNonNull(deviceId);
        Optional<Device> device = this.cache.get(deviceId);
        if (device.isEmpty()) {
            device = this.deviceRepository.findByDeviceId(deviceId);
            device.ifPresent(this.cache::put);
        }
        return device;
    }

    @Override
    public List<Device> getDevices() {
        List<Device> devices = deviceRepository.findAll();
        this.cache.putAll(devices);
        return devices;
    }

    @Override
    public List<Device> getNotPrivilegedDevices() {
        List<Device> devices = deviceRepository.findNotPrivilegedDevices();
        this.cache.putAll(devices);
        return devices;
    }

    private void connectLegacyDevice(Message message) {
        Objects.requireNonNull(message);
        Header header = message.getHeader();
        if (this.getDevice(header.getFrom()).isEmpty()) {
            this.createDevice(message);
        }
    }

    private void connectDevice(Message message) {
        Objects.requireNonNull(message);
        Header header = message.getHeader();
        //always recreate devices
        this.createDevice(message);
        Message acknowledge = Message.builder()
                .header(Header.builder()
                        .fromService(this.id())
                        .from(this.id())
                        .to(header.getFrom())
                        .priority(Priority.HIGH_PRIORITY)
                        .compressed(false)
                        .type(MessageType.CONNECT_ACK)
                        .build())
                .build();
        this.serviceRegistry.messageService()
                .publish(acknowledge);
    }

    private void createDevice(Message message) {
        Objects.requireNonNull(message);
        Header header = message.getHeader();
        Device device = this.factory.createDevice(message);
        this.deviceRepository.save(device);
        this.cache.put(device);
        this.serviceRegistry.configService()
                .getConfig()
                .getMappedAdapters()
                .get(header.getFromService())
                .getSubscriber()
                .forEach(adapterId -> this.notifyRegistrationHandlers(adapterId, device));
        this.deviceRepository.save(device);
    }

    private void disconnectDevice(Message message) {
        Objects.requireNonNull(message);
        Header header = message.getHeader();
        this.getDevice(header.getFrom()).ifPresent(this::disconnectDevice);
        Message acknowledge = Message.builder()
                .header(Header.builder()
                        .fromService(this.id())
                        .from(this.id())
                        .to(header.getFrom())
                        .priority(Priority.HIGH_PRIORITY)
                        .compressed(false)
                        .type(MessageType.DISCONNECT_ACK)
                        .build())
                .build();
        this.serviceRegistry.messageService()
                .publish(acknowledge);
    }

    private void disconnectDevice(Device device) {
        Objects.requireNonNull(device);
        device.setState(DeviceState.DISCONNECTED);
        this.deviceRepository.save(device);
    }

    private void notifyRegistrationHandlers(String adapterId, Device device) {
        Objects.requireNonNull(adapterId);
        Objects.requireNonNull(device);
        this.serviceRegistry.adapterService()
                .getAdapter(adapterId)
                .flatMap(Adapter::registrationHandler)
                .ifPresent(registrationHandler -> registrationHandler.handle(device));
    }

    private void changeQos(Message message) {
        Objects.requireNonNull(message);
        Header header = message.getHeader();
        DeviceQoSChangedPayload request = payloadParser.parse(message, DeviceQoSChangedPayload.class);
        this.getDevice(header.getFrom()).ifPresent(device -> this.changeQos(device, request.getCurrentQos()));
    }

    private void changeQos(Device device, int qos) {
        Objects.requireNonNull(device);
        device.setCurrentQos(qos);
        this.deviceRepository.save(device);
    }

}
