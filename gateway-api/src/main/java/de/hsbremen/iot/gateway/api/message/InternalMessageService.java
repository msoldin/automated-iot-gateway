package de.hsbremen.iot.gateway.api.message;

import de.hsbremen.iot.gateway.api.Service;

public interface InternalMessageService extends MessageService, Service, Runnable {

    int getAndResetReceivedMessageCount();

    int getAndResetSentMessageCount();

    MessageRouter getMessageRouter();

    MessageFilter getMessageFilter();

    MessageScheduler getMessageScheduler();

}
