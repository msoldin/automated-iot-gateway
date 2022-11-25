package de.hsbremen.iot.gateway.api.http;

import de.hsbremen.iot.gateway.api.Service;
import de.hsbremen.iot.gateway.api.adapter.HttpHandler;

public interface HttpService extends Service {

    void registerHttpHandler(HttpHandler httpHandler);

    void removeHttpHandler(HttpHandler httpHandler);
}
