package de.hsbremen.iot.gateway.api.config;

import de.hsbremen.iot.gateway.api.exception.ConfigException;

public interface ConfigService {

    GatewayConfig getConfig();

    <T extends Config> T getConfig(Class<T> tClass) throws ConfigException;

    <T extends Config> T getConfigFromBase(String identifier, Class<T> tClass) throws ConfigException;

}
