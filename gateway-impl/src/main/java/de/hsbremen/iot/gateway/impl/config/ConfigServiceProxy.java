package de.hsbremen.iot.gateway.impl.config;

import de.hsbremen.iot.gateway.api.config.Config;
import de.hsbremen.iot.gateway.api.config.ConfigService;
import de.hsbremen.iot.gateway.api.config.GatewayConfig;
import de.hsbremen.iot.gateway.api.exception.ConfigException;

public class ConfigServiceProxy implements ConfigService {

    private final ConfigService configService;

    public ConfigServiceProxy(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public GatewayConfig getConfig() {
        return this.configService.getConfig();
    }

    @Override
    public <T extends Config> T getConfig(Class<T> tClass) throws ConfigException {
        return this.configService.getConfig(tClass);
    }

    @Override
    public <T extends Config> T getConfigFromBase(String identifier, Class<T> tClass) throws ConfigException {
        return this.configService.getConfigFromBase(identifier, tClass);
    }
}
