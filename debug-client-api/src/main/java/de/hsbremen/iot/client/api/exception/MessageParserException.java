package de.hsbremen.iot.client.api.exception;

public class MessageParserException extends FormattedException {
    public MessageParserException(String message) {
        super(message);
    }

    public MessageParserException(Throwable cause) {
        super(cause);
    }

    public MessageParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageParserException(String message, Object... params) {
        super(message, params);
    }

    public MessageParserException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }
}
