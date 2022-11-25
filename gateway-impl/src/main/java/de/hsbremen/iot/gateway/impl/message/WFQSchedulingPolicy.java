package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.SchedulingPolicy;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class WFQSchedulingPolicy implements SchedulingPolicy<WeightedQueue<Message>[]> {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final int batchSize;

    public WFQSchedulingPolicy() {
        this.batchSize = DEFAULT_BATCH_SIZE;
    }

    public WFQSchedulingPolicy(int batchSize) {
        this.batchSize = batchSize < 1 ? DEFAULT_BATCH_SIZE : batchSize;
    }

    @Override
    @SafeVarargs
    public final Queue<Message> select(WeightedQueue<Message>... queues) {
        Objects.requireNonNull(queues);
        ArrayBlockingQueue<Message> batch = new ArrayBlockingQueue<>(batchSize);
        while (batch.remainingCapacity() > 0
                && Arrays.stream(queues).map(Queue::size).reduce(0, Integer::sum) != 0) {
            for(WeightedQueue<Message> queue: queues){
                if(queue.isEmpty())
                    continue;
                for(int i = 0; i < queue.getWeight(); i++){
                    Message message = queue.poll();
                    if(message == null)
                        break;
                    batch.offer(message);
                }
            }
        }
        return batch;
    }

}
