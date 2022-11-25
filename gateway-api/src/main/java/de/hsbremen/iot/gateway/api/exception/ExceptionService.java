package de.hsbremen.iot.gateway.api.exception;

import de.hsbremen.iot.gateway.api.Service;
import de.hsbremen.iot.gateway.api.adapter.Adapter;

public interface ExceptionService {

    void handleException(Exception exception);

}
