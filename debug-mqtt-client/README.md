# debug-mqtt-client

This project was bootstrapped with Maven. So be sure to install at least Maven Version 3.6.1.

## Available commands

In the project directory, you can run:

### `mvn clean`

This will clean all generated target files.

### `mvn install`

This will create the source files for this project.

The main source file for execution is located at:

`debug-mqtt-client/target/debug-mqtt-client-<version>.jar`

## Program execution

To execute the source file, just use the command prompt with:

`java -jar debug-mqtt-client-<version>.jar`

Next to the .jar there must be a configuration named `config.yaml`.

### Program configuration

The program configuration is named `config.yml`. Needed configuration properties are:

```yaml
mqtt:
    serverUri: "tcp://mqtt-broker:1883" # Domain name or IP-address of the mqtt broker
    clientId: "mqttTestClient1" # clientId for the mqtt-client, should be the same as deviceId
    gatewayHostname: "gateway" # Hostname of the gateway

debugDevice:
    deviceId: "mqttTestClient1" # deviceId in Cloud
    maxQoS: 9 # maximum QoS level defined as int
    currentQoS: 9 # current QoS level defined as int
    privileged: false # if the device is privileged
    highPriority: false # if normal messages should be high priority

debugClient:
    legacy: false # if gateway or legacy format should be used
    lowBandwidth: false # if device is low bandwidth
    roundTrip: true # if messages should return to client
    initialDelay: 1000 # initial delay before sending first message
    reportInterval: 1000 # reporting interval in ms
```