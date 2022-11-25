package de.hsbremen.iot.gateway.api.config;

import de.hsbremen.iot.gateway.api.exception.ConfigCheckException;
import de.hsbremen.iot.gateway.api.exception.ConfigPostConstructException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Base64;

public abstract class Config {

    private static final String BASE64 = "BASE64[";

    public final void configCheck() throws ConfigCheckException {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value == null && !Modifier.isTransient(field.getModifiers())) {
                    throw new ConfigCheckException("ConfigCheck failed on '%s' because field '%s' is null!",
                            this.getClass().getSimpleName(),
                            field.getName());
                } else {
                    if (value instanceof String) {
                        String parsedValue = (String) value;
                        if (parsedValue.startsWith(BASE64)) {
                            parsedValue = parsedValue.substring(BASE64.length(), parsedValue.length() - 1);
                            parsedValue = new String(Base64.getDecoder().decode(parsedValue));
                            field.set(this, parsedValue);
                        }
                    } else if (value instanceof Config) {
                        ((Config) value).configCheck();
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new ConfigCheckException("ConfigCheck failed on %s", ex, this.getClass().getSimpleName());
        }
    }

    public void postConstruct() throws ConfigPostConstructException {

    }
}
