package de.hsbremen.iot.xbee.client;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import de.hsbremen.iot.client.api.ClientAdapter;
import de.hsbremen.iot.client.api.message.Message;
import de.hsbremen.iot.client.impl.DebugMessageParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class XbeeClientAdapter implements ClientAdapter {

    private final static Logger logger = LogManager.getLogger();

    private XBeeDevice xBee;

    private XbeeClientAdapterConfig config;

    private final DebugMessageParser messageParser;

    private RemoteXBeeDevice coordinator;

    private Consumer<Message> messageConsumer;


    public XbeeClientAdapter(XbeeClientAdapterConfig config, boolean lowBandwidth) {
        this.config = config;
        this.messageParser = new DebugMessageParser(lowBandwidth);
        this.xBee = new XBeeDevice(config.getPort(), config.getBaudRate());
    }

    @Override
    public void send(Message message) {
        try {
            byte[] payload = this.messageParser.parse(message);
            this.xBee.sendData(coordinator, payload);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    @Override
    public void send(byte[] message) {
        try {
            this.xBee.sendData(coordinator, message);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    @Override
    public void onReceive(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void start() {
        try {
            xBee.open();
            XBeeNetwork network = xBee.getNetwork();
            this.coordinator = network.discoverDevice(this.config.getCoordinator());
            xBee.addDataListener(xBeeMessage -> {
                try {
                    this.messageConsumer.accept(this.messageParser.parse(xBeeMessage.getData()));
                } catch (Exception ex) {
                    logger.error(ExceptionUtils.getStackTrace(ex));
                }
            });
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
        xBee.close();
    }

}
