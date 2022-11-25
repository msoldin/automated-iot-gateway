package de.hsbremen.iot.client.api.message;

import java.util.HashMap;
import java.util.Map;

public enum Priority {

    BEST_EFFORT(0), HIGH_PRIORITY(1);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private static final Map<Integer, Priority> map = new HashMap<>();

    static {
        for (Priority priority : Priority.values()) {
            map.put(priority.value, priority);
        }
    }

    public static Priority valueOf(int value) {
        return map.get(value);
    }

}
