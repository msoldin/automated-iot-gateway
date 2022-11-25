package de.hsbremen.iot.gateway.api.adapter;

import de.hsbremen.iot.gateway.api.Service;

import java.util.Map;
import java.util.Optional;

public interface InternalAdapterService extends AdapterService, Service {

    Optional<Adapter> getAdapter(String adapterId);

    Map<String, Adapter> getAdapters();

}
