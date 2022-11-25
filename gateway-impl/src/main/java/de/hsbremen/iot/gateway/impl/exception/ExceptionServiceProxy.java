package de.hsbremen.iot.gateway.impl.exception;

import de.hsbremen.iot.gateway.api.exception.ExceptionService;

public class ExceptionServiceProxy implements ExceptionService {

    private final ExceptionService exceptionService;

    public ExceptionServiceProxy(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @Override
    public void handleException(Exception exception) {
        this.exceptionService.handleException(exception);
    }
}
