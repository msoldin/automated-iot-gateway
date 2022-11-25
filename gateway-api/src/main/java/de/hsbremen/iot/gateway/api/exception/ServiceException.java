package de.hsbremen.iot.gateway.api.exception;

import de.hsbremen.iot.gateway.api.Service;
import lombok.Getter;

public class ServiceException extends FormattedException {

    @Getter
    private final Service service;

    public ServiceException(String message, Service service) {
        super(message);
        this.service = service;
    }

    public ServiceException(Throwable cause, Service service) {
        super(cause);
        this.service = service;
    }

    public ServiceException(String message, Throwable cause, Service service) {
        super(message, cause);
        this.service = service;
    }

    public ServiceException(String message, Service service, Object... params) {
        super(message, params);
        this.service = service;
    }

    public ServiceException(String message, Throwable cause, Service service, Object... params) {
        super(message, cause, params);
        this.service = service;
    }

}
