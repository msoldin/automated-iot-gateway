package de.hsbremen.iot.gateway.api.message;

public interface MessageFilter {

    boolean filter(Message message);

    void setBestEffortFilter(boolean value);

    boolean isBestEffortFilter();

    void setHighPriorityFilter(boolean value);

    boolean isHighPriorityFilter();

    void setRegistrationFilter(boolean value);

    boolean isRegistrationFilter();

}
