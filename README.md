# gateway

This project was bootstrapped with Maven. So be sure to install at least Maven Version 3.6.1.

## Available commands

In the project directory, you can run:

### `mvn clean`

This will clean all generated target files.

### `mvn test`

This will execute all provided unit tests for all modules.
The result will be displayed in console.

### `mvn install`

This will create the source files for all modules.

The main source files for execution are located at:

`<module>/target/<module>-<version>.jar`

## Modules

### aws-mqtt-adapter

Includes the adapter for the communication with AWS over MQTT. It depends on the gateway-api.

### debug-client-api

Includes a general API for testing purposes. It was not part of the design because this library was just created to prevent code duplication in all debug clients.

### debug-mqtt-client

Includes a debugging client for MQTT. It depends on the debug-client-api.

### debug-websocket-client

Includes a debugging client for WebSocket. It depends on the debugclient-api.

### debug-xbee-client

Includes a debugging client for ZigBee. It depends on the debug-client-api.

### gateway-api

Defines the complete gateway API, which was already explained in the design. It forms the basic framework for the gateway-impl module and all realised components. This project does not include any behaviour and is just for architectural purposes.

### gateway-impl

Defines the behaviour of the gateway while using the gateway-api module as a basic framework.

### mqtt-adapter

Includes the adapter for the communication with MQTT clients. It depends on the gateway-api.

### websocket-adapter

Includes the adapter for the communication with WebSocket clients. It depends on the gateway-api.

### xbee-adapter

Includes the adapter for the communication with ZigBee clients, based on XBee hardware. It depends on the gateway-api.

### xbee-java-nrjavaserial

Includes an interface to communicate with the XBee hardware. This project was not created in this implementation. It was used from DIGI International Inc. However it was recompiled against a more recent serial communication library, called “nrjavaserial”, because the used “rxtx” library was not maintained anymore and only supported 32-bit architectures.





