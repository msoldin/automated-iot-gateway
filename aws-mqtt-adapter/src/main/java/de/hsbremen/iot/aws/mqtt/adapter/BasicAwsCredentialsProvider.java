package de.hsbremen.iot.aws.mqtt.adapter;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class BasicAwsCredentialsProvider implements AwsCredentialsProvider {

    private final AwsCredentials awsCredentials;

    public BasicAwsCredentialsProvider(AwsCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return awsCredentials;
    }
}