package de.hsbremen.iot.gateway.impl.mail;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.Service;
import de.hsbremen.iot.gateway.api.config.MailConfig;
import de.hsbremen.iot.gateway.api.exception.ServiceRuntimeException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import de.hsbremen.iot.gateway.api.mail.MailService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailServiceImpl implements MailService, Service {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "MailService";

    private String sendTo;

    private String sendFrom;

    private Session session;

    private final InternalServiceRegistry serviceRegistry;

    public MailServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        try {
            MailConfig config = this.serviceRegistry.configService().getConfig().getMail();
            Properties props = new Properties();
            props.put("mail.smtp.host", config.getHost());
            props.put("mail.smtp.auth", Boolean.toString(config.isAuthEnabled()));
            props.put("mail.smtp.port", config.getPort());
            props.put("mail.smtp.starttls.enable", Boolean.toString(config.isTlsEnabled()));
            this.session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUsername(), config.getPassword());
                }
            });
            this.sendTo = config.getSendTo();
            this.sendFrom = config.getSendFrom();
            logger.info("MailService successfully started!");
        } catch (Exception ex) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceStartupException("The MailService could not be started!", ex, this));
        }

    }

    @Override
    public void stop() {
        logger.info("MailService successfully shut down!");
    }

    @Override
    public void sendMail(String subject, String description) {
        //Fixing classloader issues in javax.mail/osgi by overriding classloader
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(javax.mail.Message.class.getClassLoader());
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(this.sendFrom);
            message.setRecipients(RecipientType.TO, this.sendTo);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(description);
            Transport.send(message);
        } catch (MessagingException e) {
            this.serviceRegistry
                    .exceptionService()
                    .handleException(new ServiceRuntimeException("Sending Mail was not possible!", e, this));
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    @Override
    public void sendMail(String subject, Exception exception) {
        this.sendMail(String.format("Gateway on %s reports: %s", this.serviceRegistry.configService().getConfig().getHostname(), subject),
                ExceptionUtils.getStackTrace(exception));
    }

}
