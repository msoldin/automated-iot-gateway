package de.hsbremen.iot.gateway.api.adapter;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface HttpHandler {

    String route();

    Handler<RoutingContext> handler();

}
