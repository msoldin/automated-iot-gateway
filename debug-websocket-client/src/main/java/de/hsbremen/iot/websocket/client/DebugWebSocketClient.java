package de.hsbremen.iot.websocket.client;

import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.Device;
import de.hsbremen.iot.client.impl.DebugClient;
import de.hsbremen.iot.client.impl.DebugDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DebugWebSocketClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        String configAsString = DebugWebSocketClient.createConfig();
        DebugWebSocketClientConfig config = new Yaml().loadAs(configAsString, DebugWebSocketClientConfig.class);
        logger.info("Starting client with : {} {}", System.lineSeparator(), configAsString);
        ClientAdapter client = new WebSocketClientAdapter(config.getWebsocket(), config.getDebugClient().isLowBandwidth());
        Device device = new DebugDevice(config.getDebugDevice());
        DebugClient debugClient = new DebugClient(client, device, config.getDebugClient());
        debugClient.start();
    }


    private static String createConfig() throws IOException {
        if (DebugWebSocketClient.isIDE()) {
            return Files.readString(Paths.get("./debug-websocket-client/config.yaml"));
        }
        return Files.readString(Paths.get("./config.yaml"));
    }

    private static boolean isIDE() {
        return !DebugWebSocketClient.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile()
                .contains(".jar");
    }

}
