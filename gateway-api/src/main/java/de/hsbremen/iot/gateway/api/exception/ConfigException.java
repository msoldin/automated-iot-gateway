package de.hsbremen.iot.gateway.api.exception;

public class ConfigException extends FormattedException{

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(String message, Object... params) {
        super(message, params);
    }

    public ConfigException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

}
