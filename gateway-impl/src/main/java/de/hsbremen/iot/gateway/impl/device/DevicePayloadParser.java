package de.hsbremen.iot.gateway.impl.device;

import com.google.gson.Gson;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.PayloadParser;

import java.nio.charset.StandardCharsets;

public class DevicePayloadParser implements PayloadParser {

    private final Gson gson = new Gson();

    @Override
    public <T> T parse(Message message, Class<T> tClass) {
        byte[] payload = message.getPayload();
        String payloadAsString = new String(payload, StandardCharsets.UTF_8);
        return gson.fromJson(payloadAsString, tClass);
    }
}
