package de.hsbremen.iot.gateway.api.device;

import lombok.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Device implements Serializable {

    private String deviceId;

    private DeviceState state;

    private int maxQos;

    private int currentQos;

    private boolean privileged;

    private String adapterId;

    private Map<String, Object> adapterInterfaces;

    public Optional<?> getAdapterInterface(Class<?> clazz) {
        return Optional.ofNullable(this.adapterInterfaces.get(clazz.getName()));
    }

    public void addDeviceAdapter(Object deviceAdapter) {
        this.adapterInterfaces.put(deviceAdapter.getClass().getName(), deviceAdapter);
    }
}
