package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AwsRegistrationHandlerConfig extends Config {

    private String region;

    private String groupArn;

    private String accessKeyId;

    private String secretAccessKey;

}
