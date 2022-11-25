package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.message.MessageRouter;
import de.hsbremen.iot.gateway.api.message.Header;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageType;

import java.util.Queue;

public class MessageRouterImpl implements MessageRouter {

    private final InternalServiceRegistry serviceRegistry;

    public MessageRouterImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void handle(Queue<Message> messages) {
        for (Message message : messages) {
            Header header = message.getHeader();
            if (header.getType() == MessageType.CONNECT
                    || header.getType() == MessageType.LEGACY_CONNECT
                    || header.getType() == MessageType.DISCONNECT
                    || header.getType() == MessageType.QOS_CHANGED) {
                this.serviceRegistry.deviceService().handle(message);
            } else if (header.getTo() != null) {
                this.serviceRegistry
                        .deviceService()
                        .getDevice(header.getTo())
                        .map(Device::getAdapterId)
                        .ifPresent(adapterId -> this.routeToAdapter(adapterId, message));
            } else {
                this.serviceRegistry.configService()
                        .getConfig()
                        .getMappedAdapters()
                        .get(header.getFromService())
                        .getSubscriber()
                        .forEach(subscriber -> this.serviceRegistry
                                .adapterService()
                                .getAdapter(subscriber)
                                .map(Adapter::id)
                                .ifPresent(adapterId -> this.routeToAdapter(adapterId, message)));
            }
        }
    }

    private void routeToAdapter(String adapterId, Message message) {
        this.serviceRegistry.adapterService()
                .getAdapter(adapterId)
                .ifPresent(adapter -> adapter.messageHandler().handle(message));
    }
}
