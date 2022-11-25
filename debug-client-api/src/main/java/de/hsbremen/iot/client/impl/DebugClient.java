package de.hsbremen.iot.client.impl;

import com.google.gson.Gson;
import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.Device;
import de.hsbremen.iot.client.api.DeviceConnectPayload;
import de.hsbremen.iot.client.api.DeviceQoSChangedPayload;
import de.hsbremen.iot.client.api.message.Header;
import de.hsbremen.iot.client.api.message.Message;
import de.hsbremen.iot.client.api.message.MessageType;
import de.hsbremen.iot.client.api.message.Priority;
import de.hsbremen.iot.client.impl.CsvStore.CsvType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugClient implements Runnable {

    private final static Logger logger = LogManager.getLogger();

    private final static Gson GSON = new Gson();

    private final CsvStore sentStore;

    private final CsvStore receivedStore;

    private final ClientAdapter client;

    private final Device device;

    private boolean connected;

    private boolean connectAttempt;

    private final DebugClientConfig config;

    public final ScheduledExecutorService executorService;

    private Future<?> runningTask;

    public DebugClient(ClientAdapter client,
                       Device device,
                       DebugClientConfig config) {
        this.client = client;
        this.device = device;
        this.config = config;
        this.sentStore = new CsvStore(CsvType.SENT);
        this.receivedStore = new CsvStore(CsvType.RECEIVE);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        this.client.start();
        this.client.onReceive(this::receive);
        this.runningTask = this.executorService
                .scheduleAtFixedRate(this,
                        this.config.getInitialDelay(),
                        this.config.getReportInterval(),
                        TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        try {
            this.executorService.shutdown();
            this.executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            if (!this.config.isLegacy()) {
                this.sendDisconnect();
            }
            this.client.stop();
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    @Override
    public void run() {
        Map<String, Object> state = this.device.getState();
        if (config.isLegacy()) {
            this.sendLegacy(state);
        } else {
            if (this.connected) {
                this.sendUpdate(state);
            } else {
                if (!this.connectAttempt) {
                    this.sendConnect();
                    this.connectAttempt = true;
                }
            }
        }
    }

    public void receive(Message message) {
        logger.info("Received message: {}", message);
        if (this.config.isLegacy()) {
            logger.info("DeviceMode is legacy, package can not be received!");
            return;
        }
        switch (message.getHeader().getMessageType()) {
            case UPDATE:
                this.onUpdate(message);
                break;
            case CONNECT_ACK:
                this.onConnectAcknowledge();
                break;
            case DISCONNECT_ACK:
                this.onDisconnectAcknowledge();
                break;
            case QOS_REDUCE:
                this.onQoSReduce(message);
                break;
        }
    }

    public void sendLegacy(Map<String, Object> state) {
        this.client.send(this.toJSONBytes(state));
    }

    public void sendGet(String deviceId) {
        try {
            if (this.config.isLowBandwidth()) {
                logger.error("GET-Request is not supported for low bandwidth!");
                return;
            }
            Header.HeaderBuilder header = Header.builder()
                    .from(this.device.deviceId())
                    .compressed(false)
                    .to(deviceId)
                    .messageType(MessageType.GET)
                    .priority(this.device.highPriority() ? Priority.HIGH_PRIORITY : Priority.BEST_EFFORT);
            if (this.config.isRoundTrip()) {
                header.to(this.device.deviceId());
            }
            Message message = Message.builder()
                    .header(header.build())
                    .build();
            this.client.send(message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public void sendUpdate(Map<String, Object> state) {
        try {
            byte[] payload = this.toJSONBytes(state);
            Header.HeaderBuilder header = Header.builder()
                    .messageType(MessageType.UPDATE)
                    .priority(this.device.highPriority() ? Priority.HIGH_PRIORITY : Priority.BEST_EFFORT)
                    .compressed(false)
                    .from(this.device.deviceId());
            if (this.config.isRoundTrip()) {
                header.to(this.device.deviceId());
            }
            Message message = Message.builder()
                    .header(header.build())
                    .payload(payload)
                    .build();
            logger.info("Send message: {}", message);
            this.sentStore.addRecord(message);
            this.client.send(message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public void sendDelete(String deviceId) {
        try {
            if (this.config.isLowBandwidth()) {
                logger.error("DELETE-Request is not supported for low bandwidth!");
                return;
            }
            Header.HeaderBuilder header = Header.builder().from(this.device.deviceId())
                    .compressed(false)
                    .to(deviceId)
                    .messageType(MessageType.DELETE)
                    .priority(this.device.highPriority() ? Priority.HIGH_PRIORITY : Priority.BEST_EFFORT);
            if (this.config.isRoundTrip()) {
                header.to(this.device.deviceId());
            }
            Message message = Message.builder()
                    .header(header.build())
                    .build();
            this.client.send(message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public void sendConnect() {
        try {
            DeviceConnectPayload deviceConnectPayload = DeviceConnectPayload.builder()
                    .currentQos(this.device.getCurrentQoS())
                    .maxQos(this.device.getMaxQoS())
                    .privileged(this.device.isPrivileged())
                    .build();
            Header.HeaderBuilder header = Header.builder()
                    .messageType(MessageType.CONNECT)
                    .priority(Priority.HIGH_PRIORITY)
                    .compressed(false)
                    .from(this.device.deviceId());
            Message message = Message.builder()
                    .header(header.build())
                    .payload(this.toJSONBytes(deviceConnectPayload))
                    .build();
            this.client.send(message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public void sendDisconnect() {
        try {
            Message message = Message.builder()
                    .header(Header.builder()
                            .messageType(MessageType.DISCONNECT)
                            .priority(Priority.HIGH_PRIORITY)
                            .compressed(false)
                            .from(this.device.deviceId())
                            .build())
                    .build();
            this.client.send(message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public void onConnectAcknowledge() {
        this.connected = true;
    }

    public void onDisconnectAcknowledge() {
        this.connected = false;
    }

    public void onUpdate(Message message) {
        this.receivedStore.addRecord(message);
    }

    public void onQoSReduce(Message message) {
        this.device.setCurrentQos(device.getCurrentQoS() == 0 ? 0 : device.getCurrentQoS() - 1);
        this.runningTask.cancel(true);
        this.executorService.scheduleAtFixedRate(this,
                this.config.getInitialDelay(),
                (long) (this.config.getReportInterval() * ((float) this.device.getMaxQoS() / (float) this.device.getCurrentQoS())),
                TimeUnit.MILLISECONDS);
        DeviceQoSChangedPayload payload = DeviceQoSChangedPayload.builder()
                .currentQos(device.getCurrentQoS())
                .build();
        Message response = Message.builder()
                .header(Header.builder()
                        .from(this.device.deviceId())
                        .messageType(MessageType.QOS_CHANGED)
                        .priority(Priority.HIGH_PRIORITY)
                        .build())
                .payload(this.toJSONBytes(payload))
                .build();
        this.client.send(response);
    }

    private byte[] toJSONBytes(Object object) {
        String json = GSON.toJson(object);
        return json.getBytes(StandardCharsets.UTF_8);
    }

}
