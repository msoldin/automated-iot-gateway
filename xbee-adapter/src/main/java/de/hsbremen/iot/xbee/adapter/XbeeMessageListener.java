package de.hsbremen.iot.xbee.adapter;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import de.hsbremen.iot.gateway.api.adapter.MessageListener;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageParser;

import java.util.concurrent.atomic.AtomicInteger;

public class XbeeMessageListener implements MessageListener {

    private final XbeeAdapter xbeeAdapter;

    private final AtomicInteger messageCount;

    public XbeeMessageListener(XbeeAdapter xbeeAdapter) {
        this.xbeeAdapter = xbeeAdapter;
        this.messageCount = new AtomicInteger(0);
    }

    @Override
    public void start() {
        XBeeDevice xbee = this.xbeeAdapter.getxBeeDevice();
        MessageParser messageParser = this.xbeeAdapter
                .getServiceRegistry()
                .messageService()
                .getMessageParser();
        xbee.addDataListener(xBeeMessage -> {
            try {
                RemoteXBeeDevice device = xBeeMessage.getDevice();
                device.readDeviceInfo(); //without reading the node id is null
                String deviceId = device.getNodeID();
                byte[] payload = xBeeMessage.getData();
                Message parsedMessage;
                if (messageParser.isLegacy(payload)) {
                    parsedMessage = messageParser.parseLegacy(payload, xbeeAdapter.id(), deviceId);
                } else {
                    parsedMessage = messageParser.parse(payload, xbeeAdapter.id(), deviceId);
                }
                this.xbeeAdapter.getServiceRegistry()
                        .messageService()
                        .publish(parsedMessage);
                this.messageCount.incrementAndGet();
            } catch (Exception ex) {
                this.xbeeAdapter.getServiceRegistry()
                        .exceptionService()
                        .handleException(ex);
            }
        });
    }

    @Override
    public void stop() {
    }

    @Override
    public int getAndResetMessageCount() {
        return this.messageCount.getAndSet(0);
    }
}
