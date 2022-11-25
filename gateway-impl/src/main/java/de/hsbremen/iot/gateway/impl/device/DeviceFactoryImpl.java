package de.hsbremen.iot.gateway.impl.device;

import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceConnectPayload;
import de.hsbremen.iot.gateway.api.device.DeviceFactory;
import de.hsbremen.iot.gateway.api.device.DeviceState;
import de.hsbremen.iot.gateway.api.message.Header;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageType;
import de.hsbremen.iot.gateway.api.message.PayloadParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class DeviceFactoryImpl implements DeviceFactory {

    private static final Logger logger = LogManager.getLogger();

    private final PayloadParser payloadParser;

    public DeviceFactoryImpl() {
        this.payloadParser = new DevicePayloadParser();
    }

    @Override
    public Device createDevice(Message message) {
        Header header = message.getHeader();
        Device.DeviceBuilder device = Device.builder()
                .deviceId(header.getFrom())
                .adapterId(header.getFromService());
        if (header.getType() == MessageType.LEGACY_CONNECT) {
            device.state(DeviceState.LEGACY);
            device.privileged(false);
            device.currentQos(0);
            device.maxQos(0);
        } else {
            device.state(DeviceState.CONNECTED);
            DeviceConnectPayload request = this.payloadParser.parse(message, DeviceConnectPayload.class);
            device.maxQos(request.getMaxQos());
            device.currentQos(request.getCurrentQos());
            device.privileged(request.isPrivileged());
        }
        device.adapterInterfaces(new HashMap<>());
        logger.info("Created device from message {}", message);
        return device.build();
    }

}
