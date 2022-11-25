package de.hsbremen.iot.gateway.api.exception;

import de.hsbremen.iot.gateway.api.Service;

public class ServiceShutdownException extends ServiceException {
    public ServiceShutdownException(String message, Service service) {
        super(message, service);
    }

    public ServiceShutdownException(Throwable cause, Service service) {
        super(cause, service);
    }

    public ServiceShutdownException(String message, Throwable cause, Service service) {
        super(message, cause, service);
    }

    public ServiceShutdownException(String message, Service service, Object... params) {
        super(message, service, params);
    }

    public ServiceShutdownException(String message, Throwable cause, Service service, Object... params) {
        super(message, cause, service, params);
    }
}
