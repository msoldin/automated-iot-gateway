package de.hsbremen.iot.gateway.api.exception;

import de.hsbremen.iot.gateway.api.Service;

public class ServiceRuntimeException extends ServiceException {

    public ServiceRuntimeException(String message, Service service) {
        super(message, service);
    }

    public ServiceRuntimeException(Throwable cause, Service service) {
        super(cause, service);
    }

    public ServiceRuntimeException(String message, Throwable cause, Service service) {
        super(message, cause, service);
    }

    public ServiceRuntimeException(String message, Service service, Object... params) {
        super(message, service, params);
    }

    public ServiceRuntimeException(String message, Throwable cause, Service service, Object... params) {
        super(message, cause, service, params);
    }
}
