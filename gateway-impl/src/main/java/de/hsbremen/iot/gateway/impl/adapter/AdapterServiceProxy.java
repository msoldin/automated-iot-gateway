package de.hsbremen.iot.gateway.impl.adapter;

import de.hsbremen.iot.gateway.api.adapter.Adapter;
import de.hsbremen.iot.gateway.api.adapter.AdapterService;

public class AdapterServiceProxy implements AdapterService {

    private final AdapterService adapterService;

    public AdapterServiceProxy(AdapterService adapterService) {
        this.adapterService = adapterService;
    }

    @Override
    public void registerAdapter(Adapter adapter) {
        this.adapterService.registerAdapter(adapter);
    }

    @Override
    public void removeAdapter(Adapter adapter) {
        this.adapterService.removeAdapter(adapter);
    }
}
