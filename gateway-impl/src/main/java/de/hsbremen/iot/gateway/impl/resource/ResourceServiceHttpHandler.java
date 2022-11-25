package de.hsbremen.iot.gateway.impl.resource;

import com.google.gson.Gson;
import de.hsbremen.iot.gateway.api.adapter.HttpHandler;
import de.hsbremen.iot.gateway.api.resource.ResourceUsageMeasurement;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class ResourceServiceHttpHandler implements HttpHandler {

    private final Gson gson;

    private ResourceUsageMeasurement state;

    public ResourceServiceHttpHandler() {
        this.gson = new Gson();
        this.state = null;
    }

    @Override
    public String route() {
        return "/gateway/resources";
    }

    @Override
    public Handler<RoutingContext> handler() {
        return ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "application/json");
            response.end(state == null ? "Nothing measured yet!" : gson.toJson(state));
        };
    }

    public ResourceUsageMeasurement getState() {
        return state;
    }

    public void setState(ResourceUsageMeasurement state) {
        this.state = state;
    }
}
