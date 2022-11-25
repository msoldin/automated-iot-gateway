package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageScheduler;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

public class WFQMessageScheduler implements MessageScheduler {

    private final WFQSchedulingPolicy schedulingPolicy;

    private final WeightedQueue<Message> bestEffortQueue;

    private final WeightedQueue<Message> highPriorityQueue;

    public WFQMessageScheduler() {
        this.schedulingPolicy = new WFQSchedulingPolicy();
        this.bestEffortQueue = new WeightedQueue<>(1);
        this.highPriorityQueue = new WeightedQueue<>(10);
    }

    @Override
    public Queue<Message> schedule() {
        return this.schedulingPolicy.select(this.highPriorityQueue, this.bestEffortQueue);
    }

    @Override
    public void publish(Message message) {
        Objects.requireNonNull(message);
        switch (message.getHeader().getPriority()) {
            case BEST_EFFORT:
                this.bestEffortQueue.add(message);
                break;
            case HIGH_PRIORITY:
                this.highPriorityQueue.add(message);
                break;
        }
    }

    @Override
    public boolean isEmpty() {
        for (WeightedQueue<Message> channel : Arrays.asList(this.bestEffortQueue, this.highPriorityQueue)) {
            if (!channel.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public int getSize() {
        int size = 0;
        for (WeightedQueue<Message> channel : Arrays.asList(this.bestEffortQueue, this.highPriorityQueue)) {
            size += channel.size();
        }
        return size;
    }
}
