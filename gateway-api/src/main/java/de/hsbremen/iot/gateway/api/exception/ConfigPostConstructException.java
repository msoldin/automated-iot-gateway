package de.hsbremen.iot.gateway.api.exception;

public class ConfigPostConstructException extends ConfigException {

    public ConfigPostConstructException(String message) {
        super(message);
    }

    public ConfigPostConstructException(Throwable cause) {
        super(cause);
    }

    public ConfigPostConstructException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigPostConstructException(String message, Object... params) {
        super(message, params);
    }

    public ConfigPostConstructException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

}
