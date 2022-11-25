package de.hsbremen.iot.gateway.api.exception;

import de.hsbremen.iot.gateway.api.Service;

public class ServiceStartupException extends ServiceException {

    public ServiceStartupException(String message, Service service) {
        super(message, service);
    }

    public ServiceStartupException(Throwable cause, Service service) {
        super(cause, service);
    }

    public ServiceStartupException(String message, Throwable cause, Service service) {
        super(message, cause, service);
    }

    public ServiceStartupException(String message, Service service, Object... params) {
        super(message, service, params);
    }

    public ServiceStartupException(String message, Throwable cause, Service service, Object... params) {
        super(message, cause, service, params);
    }
}
