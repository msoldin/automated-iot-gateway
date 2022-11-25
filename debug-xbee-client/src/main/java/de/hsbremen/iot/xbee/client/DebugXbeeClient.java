package de.hsbremen.iot.xbee.client;

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

public class DebugXbeeClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        String configAsString = DebugXbeeClient.createConfig();
        DebugXbeeClientConfig config = new Yaml().loadAs(configAsString, DebugXbeeClientConfig.class);
        logger.info("Starting client with : {} {}", System.lineSeparator(), configAsString);
        ClientAdapter client = new XbeeClientAdapter(config.getXbee(), config.getDebugClient().isLowBandwidth());
        Device device = new DebugDevice(config.getDebugDevice());
        DebugClient debugClient = new DebugClient(client, device, config.getDebugClient());
        debugClient.start();
    }

    private static String createConfig() throws IOException {
        if (DebugXbeeClient.isIDE()) {
            return Files.readString(Paths.get("./debug-xbee-client/config.yaml"));
        }
        return Files.readString(Paths.get("./config.yaml"));
    }

    private static boolean isIDE() {
        return !DebugXbeeClient.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile()
                .contains(".jar");
    }

}
