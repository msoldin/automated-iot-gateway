package de.hsbremen.iot.gateway.api.config;

import de.hsbremen.iot.gateway.api.exception.ConfigPostConstructException;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class GatewayConfig extends Config {

    private MailConfig mail;

    private VertxConfig vertx;

    private List<AdapterConfig> adapters;

    private MessageServiceConfig messaging;

    private ResourceServiceConfig monitoring;

    private transient String hostname;

    private transient Map<String, AdapterConfig> mappedAdapters;

    @Override
    public void postConstruct() throws ConfigPostConstructException {
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
            this.mappedAdapters = this.adapters
                    .stream()
                    .collect(Collectors.toMap(AdapterConfig::getAdapterId, Function.identity()));
        } catch (UnknownHostException e) {
            throw new ConfigPostConstructException("Failed to resolve hostname", e);
        }
    }
}
