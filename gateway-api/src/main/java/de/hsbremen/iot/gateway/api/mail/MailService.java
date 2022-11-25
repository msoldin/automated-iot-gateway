package de.hsbremen.iot.gateway.api.mail;

import de.hsbremen.iot.gateway.api.Service;

public interface MailService extends Service {

    void sendMail(String subject, String description);

    void sendMail(String subject, Exception exception);

}
