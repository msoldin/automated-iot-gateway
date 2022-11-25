# websocket-adapter

This project was bootstrapped with Maven. So be sure to install at least Maven Version 3.6.1.

## Available commands

In the project directory, you can run:

### `mvn clean`

This will clean all generated target files.

### `mvn install`

This will create the source files for this project.

The main source file for execution is located at:

`websocket-adapter/target/websocket-adapter-<version>.jar`

## Install

1. Publish  artifact to a local or remote Maven repository
2. Install the gateway-impl before proceeding
3. Install bundle in Apache Karaf `bundle:install mvn:de.hsbremen.iot/websocket-adapter/<version>`
4. Configure the bundle in `${user.home}/.gateway/config.yaml`

```yaml
adapters:
  - adapterId: websocket-adapter
    subscriber: [ ]
```
4. Start bundle through `bundle:start websocket-adapter`

