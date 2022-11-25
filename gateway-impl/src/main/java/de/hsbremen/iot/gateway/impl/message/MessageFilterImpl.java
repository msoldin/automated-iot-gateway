package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.message.Header;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.api.message.MessageFilter;
import de.hsbremen.iot.gateway.api.message.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageFilterImpl implements MessageFilter {

    private static final Logger logger = LogManager.getLogger();

    private final InternalServiceRegistry serviceRegistry;

    private boolean filterBestEffort;

    private boolean filterHighPriority;

    private boolean filterRegistration;

    public MessageFilterImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.filterBestEffort = false;
        this.filterHighPriority = false;
        this.filterRegistration = false;
    }

    @Override
    public boolean filter(Message message) {
        if (message == null) {
            logger.info("Denied message: Message is null!");
            return true;
        }
        if (message.getHeader() == null) {
            logger.info("Denied message: Header is null!");
            return true;
        }
        return this.isHeaderNotValid(message.getHeader());
    }

    @Override
    public void setBestEffortFilter(boolean value) {
        this.filterBestEffort = value;
    }

    @Override
    public boolean isBestEffortFilter() {
        return this.filterBestEffort;
    }

    @Override
    public void setHighPriorityFilter(boolean value) {
        this.filterHighPriority = value;
    }

    @Override
    public boolean isHighPriorityFilter() {
        return this.filterHighPriority;
    }

    @Override
    public void setRegistrationFilter(boolean value) {
        this.filterRegistration = value;
    }

    @Override
    public boolean isRegistrationFilter() {
        return this.filterRegistration;
    }

    private boolean isHeaderNotValid(Header header) {

        if (header.getFromService() == null || header.getFromService().isEmpty()) {
            logger.info("Denied message: FromService is null, message is not resolvable!");
            return true;
        }

        if (header.getFrom() == null || header.getFrom().isEmpty()) {
            logger.info("Denied message: From is null, message is not resolvable!");
            return true;
        }

        if (header.getPriority() == null) {
            logger.info("Denied message: Priority is null, message is not resolvable!");
            return true;
        }

        if (header.getType() == null) {
            logger.info("Denied message: MessageType is null, message is not resolvable!");
            return true;
        }


        switch (header.getType()) {
            case QOS_INCREASE:
            case QOS_REDUCE:
            case DISCONNECT_ACK:
            case CONNECT_ACK:
                if (serviceRegistry.deviceService().getDevice(header.getFrom()).isPresent()) {
                    logger.info("Denied message: MessageType is not allowed from clients!");
                    return true;
                }
                return false;
            case CONNECT:
            case LEGACY_CONNECT:
                if (this.filterRegistration) {
                    logger.info("Denied message: Registration filter is enabled!");
                    return true;
                }
                return false;
            //always allowed
            case DISCONNECT:
            case QOS_CHANGED:
                return false;
            default:
                if (serviceRegistry.deviceService().getDevice(header.getFrom()).isEmpty()) {
                    logger.info("Denied message: Device not registered!");
                    return true;
                }
        }

        if (this.filterBestEffort && header.getPriority() == Priority.BEST_EFFORT) {
            logger.info("Denied message: BestEffort filter is enabled!");
            return true;
        }

        if (this.filterHighPriority && header.getPriority() == Priority.HIGH_PRIORITY) {
            logger.info("Denied message: HighPriority filter is enabled!");
            return true;
        }

        return false;
    }


}
