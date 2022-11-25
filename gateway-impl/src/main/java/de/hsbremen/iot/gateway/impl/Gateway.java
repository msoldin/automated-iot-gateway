package de.hsbremen.iot.gateway.impl;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.message.Message;
import de.hsbremen.iot.gateway.impl.message.WeightedQueue;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Gateway implements BundleActivator {

    private BundleContext bundleContext;

    private InternalServiceRegistry serviceRegistry;

    private ServiceRegistration<ServiceRegistry> serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.serviceRegistry = new ServiceRegistryImpl();
        this.serviceRegistry.start();
        this.serviceRegistration = this.bundleContext.registerService(ServiceRegistry.class, new ServiceRegistryProxy(this.serviceRegistry), new Hashtable<>());
    }

    @Override
    public void stop(BundleContext bundleContext) {
        this.serviceRegistration.unregister();
        this.serviceRegistry.stop();
        this.bundleContext = null;
    }
}
