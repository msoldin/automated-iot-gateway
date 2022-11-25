package de.hsbremen.iot.gateway.api.adapter;

import de.hsbremen.iot.gateway.api.Service;

import java.util.Optional;

public interface Adapter extends Service {

    MessageHandler messageHandler();

    MessageListener messageListener();

    Optional<HttpHandler> httpHandler();

    Optional<RegistrationHandler> registrationHandler();

}
