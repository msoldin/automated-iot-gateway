package de.hsbremen.iot.xbee.adapter;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import de.hsbremen.iot.gateway.api.adapter.MessageHandler;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class XbeeMessageHandler implements MessageHandler {

    private boolean isRunning;

    private final XbeeAdapter xbeeAdapter;

    private final Queue<Message> queue;

    private final AtomicInteger messageCount;

    public XbeeMessageHandler(XbeeAdapter xbeeAdapter) {
        this.xbeeAdapter = xbeeAdapter;
        this.isRunning = false;
        this.queue = new LinkedBlockingQueue<>();
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        new Thread(this).start();
    }

    @Override
    public void stop() {
        this.isRunning = false;
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public int getSentQueueSize() {
        return this.queue.size();
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }

    @Override
    public void handle(Message message) {
        this.queue.offer(message);
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        try {
            this.isRunning = true;
            MessageParser messageParser = this.xbeeAdapter.getServiceRegistry().messageService().getMessageParser();
            XBeeDevice xbee = this.xbeeAdapter.getxBeeDevice();
            while (this.isRunning) {
                while(!this.queue.isEmpty()){
                    try {
                        Message message = queue.poll();
                        byte[] parsedMessage = messageParser.parseForLowBandwidth(message);
                        if (message.getHeader().getTo() == null) {
                            xbee.sendBroadcastData(parsedMessage);
                        } else {
                            RemoteXBeeDevice remoteDevice = xbee.getNetwork().discoverDevice(message.getHeader().getTo());
                            xbee.sendData(remoteDevice, parsedMessage);
                        }
                        this.messageCount.incrementAndGet();
                    } catch (Exception ex) {
                        this.xbeeAdapter.getServiceRegistry()
                                .exceptionService()
                                .handleException(ex);
                    }
                }
                synchronized (this) {
                    this.wait();
                }
            }
        } catch (Exception ex) {
            this.xbeeAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceRuntimeException(ex, this.xbeeAdapter));
        }
    }
}
