# Enabling IoT Connectivity and Interoperability by using Automated Gateways
-------
<center> Jasminka Matevska and Marvin Soldin </center>
<center> City University of Applied Sciences, Flughafenallee 10, 28199 Bremen, Germany </center> 
<center> jasminka.matevska@hs-bremen.de </center> 
<center> marvin.soldin@stud.hs-bremen.de </center> 


> As an essential part of the Industry 4.0 strategy, the In-
> ternet of Things is developing to “Internet of Everything”. The number
> of interconnected devices and the amount of data produced increases
> constantly. Various devices are communicating using various protocols,
> exchanging data using various data formats and connecting to various
> software applications. In an IoT-Architecture, a gateway is a fundamen-
> tal component needed for enabling device interoperability. While a great
> deal of research has already been done on IoT in cloud computing, fog
> computing, and edge computing, there is still no intensive activity in
> the field of gateways in particular. Even the basic gateways can act as
> a proxy between low-end IoT devices and data centres, automated gate-
> ways can provide significantly higher functionality to solve the problems
> of diversity of protocols, data formats and the custom needs of various
> devices including used applications. This paper presents a concept of an
> automated gateway dealing with the problems of protocol conversion,
> device management, middleware abstraction, resource management and
> traffic optimisation. The gateway is designed as a modular plug-and-
> play architecture and was evaluated for MQTT, ZigBee, WebSocket and
> Amazon WebServices. The architecture can be extended by including ad-
> ditional modules to support further protocols and services. Finally, the
> gateway defines its protocol and translates incoming messages into an
> optional uniform format, which can be used by the client to enable more
> complex message flows. Thus, it provides a solid foundation for further
> development towards standardization of communication interfaces and
> interoperability of IoT devices.

-------
# Project

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

-------
# License

MIT License

Copyright (c) 2022 Marvin Soldin

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



