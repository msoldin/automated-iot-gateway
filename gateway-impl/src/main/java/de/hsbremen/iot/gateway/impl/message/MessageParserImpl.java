package de.hsbremen.iot.gateway.impl.message;

import de.hsbremen.iot.gateway.api.InternalServiceRegistry;
import de.hsbremen.iot.gateway.api.ServiceRegistry;
import de.hsbremen.iot.gateway.api.device.Device;
import de.hsbremen.iot.gateway.api.exception.MessageParserException;
import de.hsbremen.iot.gateway.api.message.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MessageParserImpl implements MessageParser {

    private final ServiceRegistry serviceRegistry;

    public MessageParserImpl(InternalServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public boolean isLegacy(byte[] message) throws MessageParserException {
        try {
            if (message.length < 4) {
                return true;
            }
            ByteBuffer buffer = ByteBuffer.wrap(message);
            byte[] preamble = this.extractBytes(buffer, 4);
            return !Arrays.equals(preamble, Header.PREAMBLE);
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public byte[] parse(Message message) throws MessageParserException {
        try {
            Header header = message.getHeader();
            if (header.getType() == MessageType.LEGACY) {
                return message.getPayload();
            }
            byte[] payload = message.getPayload();
            if (payload == null) {
                payload = new byte[0];
            }
            return ByteBuffer.allocate(payload.length + Header.MAX_HEADER_SIZE)
                    .put(Header.PREAMBLE)
                    .put(Header.VERSION)
                    .put(this.priorityToBytes(header.getPriority()))
                    .put(this.typeToBytes(header.getType()))
                    .put(this.booleanToByte(header.isCompressed()))
                    .put(this.booleanToByte(true))
                    .put(this.stringToBytes(header.getFrom()))
                    .put(this.stringToBytes(header.getTo()))
                    .put(payload)
                    .array();
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public byte[] parseToLegacy(Message message) throws MessageParserException {
        try {
            Header header = message.getHeader();
            if (header.getType() == MessageType.LEGACY) {
                return message.getPayload();
            }
            byte[] payload = message.getPayload();
            if (header.isCompressed()) {
                payload = this.decompress(payload);
            }
            return payload;
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public byte[] parseForLowBandwidth(Message message) throws MessageParserException {
        try {
            Header header = message.getHeader();
            if (header.getType() == MessageType.LEGACY) {
                return message.getPayload();
            }
            byte[] payload = message.getPayload();
            if (payload == null) {
                payload = new byte[0];
            } else {
                payload = header.isCompressed() ? message.getPayload() : this.compress(message.getPayload());
            }
            return ByteBuffer.allocate(payload.length + Header.MIN_HEADER_SIZE)
                    .put(Header.PREAMBLE)
                    .put(Header.VERSION)
                    .put(this.priorityToBytes(header.getPriority()))
                    .put(this.typeToBytes(header.getType()))
                    .put(this.booleanToByte(true))
                    .put(this.booleanToByte(false))
                    .put(payload)
                    .array();
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public Message parse(byte[] message, String fromService) throws MessageParserException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            this.extractBytes(buffer, 8); //remove useless version in this case, multi version support is not implemented
            Header.HeaderBuilder header = Header.builder()
                    .fromService(fromService)
                    .priority(this.bytesToPriority(buffer))
                    .type(this.bytesToMessageType(buffer));
            boolean compressed = this.byteToCompressed(buffer);
            boolean unzip = this.serviceRegistry.configService()
                    .getConfig()
                    .getMessaging()
                    .isUnzipIncomingMessages();
            if (unzip) {
                header.compressed(false);
            } else {
                header.compressed(compressed);
            }
            byte optional = buffer.get();
            if (optional == (byte) 1) {
                header.from(this.bytesToString(buffer));
                header.to(this.bytesToString(buffer));
            }
            byte[] payload = this.extractBytes(buffer, buffer.remaining());
            if (unzip && compressed) {
                payload = this.decompress(payload);
            }
            return Message.builder()
                    .header(header.build())
                    .payload(payload)
                    .build();
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public Message parse(byte[] message, String fromService, String fromDevice) throws MessageParserException {
        try {
            Message parsedMessage = this.parse(message, fromService);
            parsedMessage.getHeader().setFrom(fromDevice);
            return parsedMessage;
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    @Override
    public Message parseLegacy(byte[] message, String fromService, String fromDevice) throws MessageParserException {
        try {
            Header.HeaderBuilder header = Header.builder()
                    .fromService(fromService)
                    .from(fromDevice)
                    .compressed(false);
            Optional<Device> device = this.serviceRegistry.deviceService()
                    .getDevice(fromDevice);
            if (device.isEmpty()) {
                header.priority(Priority.HIGH_PRIORITY)
                        .type(MessageType.LEGACY_CONNECT);
                return Message.builder()
                        .header(header.build())
                        .payload(null)
                        .build();
            } else {
                header.priority(Priority.BEST_EFFORT)
                        .type(MessageType.LEGACY);
                return Message.builder()
                        .header(header.build())
                        .payload(message)
                        .build();
            }
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    private byte[] priorityToBytes(Priority priority) {
        return ByteBuffer.allocate(4).putInt(priority.getValue()).array();
    }

    private byte[] typeToBytes(MessageType type) {
        return ByteBuffer.allocate(4).putInt(type.getValue()).array();
    }

    private byte booleanToByte(boolean bool) {
        return bool ? (byte) 1 : (byte) 0;
    }

    private byte[] stringToBytes(String string) {
        byte[] bytes = new byte[256];
        if (string == null) {
            return bytes;
        }
        byte[] extractedBytes = string.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(extractedBytes, 0, bytes, 0, extractedBytes.length);
        return bytes;
    }

    private Priority bytesToPriority(ByteBuffer buffer) {
        return Priority.valueOf(buffer.getInt());
    }

    private MessageType bytesToMessageType(ByteBuffer buffer) {
        return MessageType.valueOf(buffer.getInt());
    }

    private boolean byteToCompressed(ByteBuffer buffer) {
        return buffer.get() == (byte) 1;
    }

    private String bytesToString(ByteBuffer buffer) {
        String string = new String(this.extractBytes(buffer, 256), StandardCharsets.UTF_8)
                .replaceAll("\u0000.*", "");
        if (string.isEmpty()) {
            return null;
        }
        return string;
    }

    private byte[] extractBytes(ByteBuffer buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    private byte[] compress(byte[] uncompressedData) throws MessageParserException {
        byte[] result;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos) {{
                 this.def.setLevel(Deflater.BEST_COMPRESSION);
             }}) {
            gzipOS.write(uncompressedData);
            // You need to close it before using bos
            gzipOS.close();
            result = bos.toByteArray();
        } catch (IOException e) {
            throw new MessageParserException(e);
        }
        return result;
    }

    private byte[] decompress(byte[] compressedData) throws MessageParserException {
        byte[] result;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzipIS = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            result = bos.toByteArray();
        } catch (IOException e) {
            throw new MessageParserException(e);
        }
        return result;
    }

}
