package de.hsbremen.iot.client.api.message;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

    LEGACY(0), GET(1), UPDATE(2), DELETE(3), //Client packages
    OK(100), //Response Packages
    LEGACY_CONNECT(200), CONNECT(201), DISCONNECT(202), QOS_CHANGED(203),  //Incoming control packages
    CONNECT_ACK(300), DISCONNECT_ACK(301), QOS_REDUCE(302), QOS_INCREASE(303); //Outgoing Control packages

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private static final Map<Integer, MessageType> map = new HashMap<>();

    static {
        for (MessageType messageType : MessageType.values()) {
            map.put(messageType.value, messageType);
        }
    }

    public static MessageType valueOf(int value) {
        return map.get(value);
    }

}
