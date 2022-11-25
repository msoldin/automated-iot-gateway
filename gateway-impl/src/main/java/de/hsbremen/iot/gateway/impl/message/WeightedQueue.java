package de.hsbremen.iot.gateway.impl.message;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WeightedQueue<E> implements Queue<E> {

    private final int weight;

    private final Queue<E> queue;

    public WeightedQueue(int weight) {
        this.weight = weight;
        this.queue = new LinkedBlockingQueue<>();
    }

    public int getWeight() {
        return this.weight;
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.queue.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.queue.iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.queue.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return this.queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.queue.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return this.queue.toArray(generator);
    }

    @Override
    public boolean add(E e) {
        return this.queue.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return this.queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.queue.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return this.queue.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.queue.retainAll(c);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return this.queue.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return this.queue.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return this.queue.parallelStream();
    }

    @Override
    public boolean offer(E e) {
        return this.queue.offer(e);
    }

    @Override
    public E remove() {
        return this.queue.remove();
    }

    @Override
    public E poll() {
        return this.queue.poll();
    }

    @Override
    public E element() {
        return this.queue.element();
    }

    @Override
    public E peek() {
        return this.queue.peek();
    }
}
