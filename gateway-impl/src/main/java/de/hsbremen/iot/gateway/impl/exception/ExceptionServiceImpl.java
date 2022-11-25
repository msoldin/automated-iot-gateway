package de.hsbremen.iot.gateway.impl.exception;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.Service;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.exception.InternalExceptionService;
import de.hsbremen.iot.gateway.api.exception.ServiceException;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.mail.MailService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ExceptionServiceImpl implements InternalExceptionService {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "ExceptionService";

    private final InternalServiceRegistry serviceRegistry;

    public ExceptionServiceImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void start() {
        logger.info("ExceptionService successfully started!");
    }

    @Override
    public void stop() {
        logger.info("ExceptionService successfully shut down!");
    }

    @Override
    public void handleException(Exception exception) {
        Objects.requireNonNull(exception);
        logger.error(ExceptionUtils.getStackTrace(exception));
        if (exception instanceof ServiceException) {
            if (!(exception instanceof ServiceShutdownException)) {
                ServiceException serviceException = (ServiceException) exception;
                if (serviceException.getService() instanceof Adapter) {
                    Adapter adapter = (Adapter) serviceException.getService();
                    this.serviceRegistry
                            .mailService()
                            .sendMail(String.format("Fatal exception in %s, shutting down adapter!", adapter.id()), ExceptionUtils.getStackTrace(exception));
                    this.serviceRegistry
                            .adapterService()
                            .removeAdapter(adapter);
                } else {
                    Service service = serviceException.getService();
                    if (!(service instanceof MailService)) {
                        this.serviceRegistry
                                .mailService()
                                .sendMail(String.format("Fatal exception in %s, shutting down gateway!", service.id()), ExceptionUtils.getStackTrace(exception));
                    }
                    logger.error("Can not recover from stacktrace, shutting down gateway!");
                    System.exit(-1);
                }
            }
        }
    }

}
