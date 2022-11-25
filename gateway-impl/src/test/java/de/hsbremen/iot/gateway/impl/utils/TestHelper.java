package de.hsbremen.iot.gateway.impl.utils;

import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.device.DeviceState;
import de.hsbremen.iot.gateway.api.message.Header;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageType;
import de.hsbremen.iot.gateway.api.message.Priority;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

public class TestHelper {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
    public static final String FROM = "testClient1";
    public static final String FROM_SERVICE = "testService";

    public static final String HOSTNAME = "testing";

    public static Message createRandomMessage() {
        Header header = Header.builder()
                .from(FROM)
                .priority(Priority.BEST_EFFORT)
                .type(MessageType.UPDATE)
                .compressed(false)
                .fromService(FROM_SERVICE)
                .build();
        return Message.builder()
                .header(header)
                .payload(createRandomPayload(255))
                .build();
    }

    public static Message createConnectMessage(String deviceId, int maxQos, int currentQos, boolean privileged) {
        String payload = String.format("{maxQos: %d, currentQos: %d, privileged: %s}", maxQos, currentQos, privileged);
        Header header = Header.builder()
                .from(deviceId)
                .priority(Priority.HIGH_PRIORITY)
                .type(MessageType.CONNECT)
                .compressed(false)
                .fromService("testService")
                .build();
        return Message.builder()
                .header(header)
                .payload(payload.getBytes())
                .build();
    }

    public static Device createDevice() {
        return new Device(FROM, DeviceState.CONNECTED, 0, 0, false, FROM_SERVICE, new HashMap<>());
    }

    public static byte[] createRandomPayload(int length) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

}
