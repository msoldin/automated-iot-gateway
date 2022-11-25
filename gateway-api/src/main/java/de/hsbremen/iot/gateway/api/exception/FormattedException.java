package de.hsbremen.iot.gateway.api.exception;

public class FormattedException extends Exception {

    public FormattedException(String message) {
        super(message);
    }

    public FormattedException(Throwable cause) {
        super(cause);
    }

    public FormattedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormattedException(String message, Object... params) {
        super(String.format(message, params));
    }

    public FormattedException(String message, Throwable cause, Object... params) {
        super(String.format(message, params), cause);
    }

}
