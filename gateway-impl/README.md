# gateway-impl

This project was bootstrapped with Maven. So be sure to install at least Maven Version 3.6.1.

## Available commands

In the project directory, you can run:

### `mvn clean`

This will clean all generated target files.

### `mvn test`

This will execute all provided unit tests for all modules.
The result will be displayed in console.

### `mvn install`

This will create the source files for this project.

The main source file for execution is located at:

`gateway-impl/target/gateway-impl-<version>.jar`

## Install

1. Publish  artifact to a local or remote Maven repository
2. Install feature in Apache Karaf
```bash
feature:repo-add mvn:de.hsbremen.iot/gateway-impl/<version>/xml/features
feature:install gateway-impl
```
3. Configure the gateway in `${user.home}/.gateway/config.yaml`

```yaml
vertx:
  port: 8080

mail:
  port: 587
  host: smtp-mail.outlook.com
  authEnabled: true
  tlsEnabled: true
  username: test@outlook.de
  password: BASE64[dGVzdA==]
  sendTo: test@outlook.de
  sendFrom: test@outlook.de

messaging:
  unzipIncomingMessages: true
  messageInterceptorEnabled: true

monitoring:
  interval: 10000
  primaryMemoryThreshold: 0.60
  secondaryMemoryThreshold: 0.80
  tertiaryMemoryThreshold: 0.90
  primaryCpuThreshold: 0.60
  secondaryCpuThreshold: 0.70
  tertiaryCpuThreshold: 0.90

adapters:
  - adapterId: mqtt-adapter
    subscriber:
      - aws-mqtt-adapter
  - adapterId: xbee-adapter
    subscriber:
      - aws-mqtt-adapter
  - adapterId: websocket-adapter
    subscriber:
      - aws-mqtt-adapter
  - adapterId: aws-mqtt-adapter
    subscriber: [ ]

mqtt-adapter:
  serverUri: "tcp://192.168.0.70:1883"
  cleanSession: true
  automaticReconnect: true

xbee-adapter:
  port: "COM4"
  baudRate: 9600

aws-mqtt-adapter:
  mqttConnection:
    clientId: "DevGateway"
    endpoint: "dGVzdA-ats.iot.eu-central-1.amazonaws.com"
    certPath: "C:\\test\\.gateway\\certificate.pem.crt"
    keyPath: "C:\\test\\.gateway\\private.pem.key"
    deviceShadowEnabled: true
  registrationHandler:
    region: "eu-central-1"
    groupArn: "arn:aws:iot:eu-central-1:431099744266:thinggroup/DevGateway"
    accessKeyId: "BASE64[dGVzdA==]"
    secretAccessKey: "BASE64[dGVzdA==]"
```
4. Start feature through `feature:start gateway-impl`

