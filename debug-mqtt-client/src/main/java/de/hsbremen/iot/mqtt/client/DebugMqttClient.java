package de.hsbremen.iot.mqtt.client;

import com.sun.management.OperatingSystemMXBean;
import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.Device;
import de.hsbremen.iot.client.impl.DebugClient;
import de.hsbremen.iot.client.impl.DebugDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.util.Debug;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugMqttClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws MqttException, IOException {
        String configAsString = DebugMqttClient.createConfig();
        DebugMqttClientConfig config = new Yaml().loadAs(configAsString, DebugMqttClientConfig.class);
        logger.info("Starting client with : {} {}", System.lineSeparator(), configAsString);
        ClientAdapter client = new MqttClientAdapter(config.getMqtt(), config.getDebugClient().isLowBandwidth());
        Device device = new DebugDevice(config.getDebugDevice());
        DebugClient debugClient = new DebugClient(client, device, config.getDebugClient());
        debugClient.start();
    }

    private static String createConfig() throws IOException {
        if (DebugMqttClient.isIDE()) {
            return Files.readString(Paths.get("./debug-mqtt-client/config.yaml"));
        }
        return Files.readString(Paths.get("./config.yaml"));
    }

    private static boolean isIDE() {
        return !DebugMqttClient.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile()
                .contains(".jar");
    }

}
