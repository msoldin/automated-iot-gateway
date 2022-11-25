package de.hsbremen.iot.xbee.adapter;

import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.adapter.Adapter;
import org.osgi.framework.*;

public class XbeeBundle implements BundleActivator, ServiceListener {

    private Adapter adapter;

    private BundleContext ctx;

    private ServiceReference<ServiceRegistry> serviceReference;

    @Override
    public void start(BundleContext bundleContext) {
        this.ctx = bundleContext;
        this.serviceReference = this.ctx.getServiceReference(ServiceRegistry.class);
        if (this.serviceReference != null) {
            ServiceRegistry serviceRegistry = this.ctx.getService(serviceReference);
            this.adapter = new XbeeAdapter(serviceRegistry);
            serviceRegistry.adapterService().registerAdapter(this.adapter);
        }
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (this.serviceReference != null) {
            ServiceRegistry serviceRegistry = this.ctx.getService(serviceReference);
            serviceRegistry.adapterService().removeAdapter(this.adapter);
            this.ctx.ungetService(this.serviceReference);
        }
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        switch (serviceEvent.getType()) {
            case ServiceEvent.REGISTERED:
                if (this.serviceReference != null) {
                    ServiceRegistry serviceRegistry = this.ctx.getService(serviceReference);
                    this.adapter = new XbeeAdapter(serviceRegistry);
                    serviceRegistry.adapterService().registerAdapter(this.adapter);
                }
                break;
            case ServiceEvent.UNREGISTERING:
                if (this.serviceReference != null) {
                    ServiceRegistry serviceRegistry = this.ctx.getService(serviceReference);
                    serviceRegistry.adapterService().removeAdapter(this.adapter);
                }
                break;
        }
    }
}
