package de.hsbremen.iot.aws.mqtt.adapter;

import de.hsbremen.iot.gateway.api.adapter.RegistrationHandler;
import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.exception.ServiceShutdownException;
import de.hsbremen.iot.gateway.api.exception.ServiceStartupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.AddThingToThingGroupRequest;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.DescribeThingRequest;

public class AwsRegistrationHandler implements RegistrationHandler {

    private final static Logger logger = LogManager.getLogger();

    private IotClient client;

    private AwsRegistrationHandlerConfig config;

    private final AwsMqttAdapter awsMqttAdapter;

    public AwsRegistrationHandler(AwsMqttAdapter awsMqttAdapter) {
        this.awsMqttAdapter = awsMqttAdapter;
    }

    void start() {
        try {
            this.config = this.awsMqttAdapter
                    .getAwsMqttAdapterConfig()
                    .getRegistrationHandler();
            this.client = this.createIotClient();
            logger.info("AwsRegistrationHandler successfully started!");
        } catch (Exception ex) {
            this.awsMqttAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceStartupException("The AwsRegistrationHandler could not be started ", ex, this.awsMqttAdapter));
        }
    }

    void stop() {
        try {
            this.client.close();
            logger.info("AwsRegistrationHandler successfully shut down!");
        } catch (Exception ex) {
            this.awsMqttAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(new ServiceShutdownException("The AwsRegistrationHandler could not be shut down ", ex, this.awsMqttAdapter));
        }
    }

    @Override
    public void handle(Device device) {
        try {
            if (this.isExisting(device.getDeviceId())) {
                return;
            }
            CreateThingResponse createThingResponse = this.createThing(device.getDeviceId());
            this.addThingToThingGroup(createThingResponse.thingArn(), this.config.getGroupArn());
        } catch (Exception ex) {
            this.awsMqttAdapter
                    .getServiceRegistry()
                    .exceptionService()
                    .handleException(ex);
        }
    }

    private IotClient createIotClient() {
        return IotClient
                .builder()
                .region(Region.of(this.config.getRegion()))
                .credentialsProvider(new BasicAwsCredentialsProvider(new BasicAwsCredentials(this.config.getAccessKeyId(), this.config.getSecretAccessKey())))
                .build();
    }

    private boolean isExisting(String thingName) {
        try {
            DescribeThingRequest request = DescribeThingRequest
                    .builder()
                    .thingName(thingName)
                    .build();
            client.describeThing(request);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    private CreateThingResponse createThing(String deviceId) {
        CreateThingRequest request = CreateThingRequest
                .builder()
                .thingName(deviceId)
                .build();
        return this.client.createThing(request);
    }

    private void addThingToThingGroup(String thingArn, String groupArn) {
        AddThingToThingGroupRequest request = AddThingToThingGroupRequest.builder()
                .thingArn(thingArn)
                .thingGroupArn(groupArn)
                .build();
        this.client.addThingToThingGroup(request);
    }

}
