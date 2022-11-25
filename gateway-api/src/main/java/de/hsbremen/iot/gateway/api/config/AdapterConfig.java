package de.hsbremen.iot.gateway.api.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdapterConfig extends Config {

    private String adapterId;

    private List<String> subscriber;

}
