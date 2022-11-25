# aws-mqtt-adapter

This project was bootstrapped with Maven. So be sure to install at least Maven Version 3.6.1.

## Available commands

In the project directory, you can run:

### `mvn clean`

This will clean all generated target files.

### `mvn install`

This will create the source files for this project.

The main source file for execution is located at:

`aws-mqtt-adapter/target/aws-mqtt-adapter-<version>.jar`

## Install

1. Publish  artifact to a local or remote Maven repository
2. Install the gateway-impl before proceeding
3. Install bundle in Apache Karaf `bundle:install mvn:de.hsbremen.iot/aws-mqtt-adapter/<version>`
4. Configure the bundle in `${user.home}/.gateway/config.yaml`

```yaml
adapters:
  - adapterId: aws-mqtt-adapter
    subscriber: [ ]
    
aws-mqtt-adapter:
  mqttConnection:
    clientId: "DevGateway"
    endpoint: "a2sn3h9hqsahkf-ats.iot.eu-central-1.amazonaws.com"
    certPath: "C:\\test\\.gateway\\certificate.pem.crt"
    keyPath: "C:\\test\\.gateway\\private.pem.key"
    deviceShadowEnabled: true
  registrationHandler:
    region: "eu-central-1"
    groupArn: "arn:aws:iot:eu-central-1:431099744266:thinggroup/DevGateway"
    accessKeyId: "BASE64[dGVzdA==]"
    secretAccessKey: "BASE64[dGVzdA==]"
```
4. Start bundle through `bundle:start aws-mqtt-adapter`

