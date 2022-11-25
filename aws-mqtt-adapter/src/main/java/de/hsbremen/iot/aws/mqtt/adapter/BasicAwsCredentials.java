package de.hsbremen.iot.aws.mqtt.adapter;

import software.amazon.awssdk.auth.credentials.AwsCredentials;

public class BasicAwsCredentials implements AwsCredentials {

    private final String accessKeyId;

    private final String secretAccessKey;

    public BasicAwsCredentials(String accessKeyId, String secretAccessKey) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

    @Override
    public String accessKeyId() {
        return accessKeyId;
    }

    @Override
    public String secretAccessKey() {
        return secretAccessKey;
    }
}
