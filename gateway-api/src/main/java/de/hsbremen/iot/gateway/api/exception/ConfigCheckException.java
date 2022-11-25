package de.hsbremen.iot.gateway.api.exception;

public class ConfigCheckException extends ConfigException {

    public ConfigCheckException(String message) {
        super(message);
    }

    public ConfigCheckException(Throwable cause) {
        super(cause);
    }

    public ConfigCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigCheckException(String message, Object... params) {
        super(message, params);
    }

    public ConfigCheckException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
