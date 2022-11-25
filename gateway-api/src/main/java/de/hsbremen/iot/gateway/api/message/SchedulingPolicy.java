package de.hsbremen.iot.gateway.api.message;

import java.util.Queue;

public interface SchedulingPolicy<T> {

    Queue<Message> select(T t);
}
