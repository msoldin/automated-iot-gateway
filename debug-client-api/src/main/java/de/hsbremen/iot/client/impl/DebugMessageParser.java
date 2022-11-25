package de.hsbremen.iot.client.impl;

import de.hsbremen.iot.client.api.exception.MessageParserException;
import de.hsbremen.iot.client.api.message.Header;
import de.hsbremen.iot.client.api.message.Message;
import de.hsbremen.iot.client.api.message.MessageType;
import de.hsbremen.iot.client.api.message.Priority;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DebugMessageParser {

    private boolean lowBandwidth;

    public DebugMessageParser(boolean lowBandwidth) {
        this.lowBandwidth = lowBandwidth;
    }

    public byte[] parse(Message message) throws MessageParserException {
        try {
            Header header = message.getHeader();
            if (header.getMessageType() == MessageType.LEGACY) {
                return message.getPayload();
            }
            byte[] payload = message.getPayload();
            if (payload == null) {
                payload = new byte[0];
            }
            if (this.lowBandwidth) {
                payload = header.isCompressed() ? payload : this.compress(payload);
                header.setCompressed(true);
            }
            ByteBuffer buffer = ByteBuffer.allocate(payload.length +
                            (this.lowBandwidth ? Header.MIN_HEADER_SIZE : Header.MAX_HEADER_SIZE))
                    .put(Header.PREAMBLE)
                    .put(Header.VERSION)
                    .put(this.priorityToBytes(header.getPriority()))
                    .put(this.typeToBytes(header.getMessageType()))
                    .put(this.booleanToByte(header.isCompressed()));
            if (this.lowBandwidth) {
                buffer.put(this.booleanToByte(false));
            } else {
                buffer.put(this.booleanToByte(true))
                        .put(this.stringToBytes(header.getFrom()))
                        .put(this.stringToBytes(header.getTo()));
            }
            return buffer.put(payload)
                    .array();
        } catch (Exception ex) {
            throw new MessageParserException(ex);
        }
    }

    public Message parse(byte[] message) throws MessageParserException {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            this.extractBytes(buffer, 8);
            Header.HeaderBuilder header = Header.builder()
                    .priority(this.bytesToPriority(buffer))
                    .messageType(this.bytesToMessageType(buffer))
                    .compressed(this.byteToCompressed(buffer));
            byte optional = buffer.get();
            if (optional == (byte) 1) {
                header.from(this.bytesToString(buffer));
                header.to(this.bytesToString(buffer));
            }
            return Message.builder()
                    .header(header.build())
                    .payload(this.extractBytes(buffer, buffer.remaining()))
                    .build();
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

    public byte[] compress(byte[] uncompressedData) throws MessageParserException {
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

    public byte[] decompress(byte[] compressedData) throws MessageParserException {
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
