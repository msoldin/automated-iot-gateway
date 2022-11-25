package de.hsbremen.iot.client.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hsbremen.iot.client.api.message.Header;
import de.hsbremen.iot.client.api.message.Message;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CsvStore {

    private final static Logger logger = LogManager.getLogger();

    public enum CsvType {
        SENT("sent.csv"), RECEIVE("receive.csv");

        private final String type;

        CsvType(String type) {
            this.type = type;
        }

    }

    private CsvType csvType;

    private CSVPrinter csvPrinter;

    public CsvStore(CsvType csvType) {
        try {
            this.csvType = csvType;
            CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("Priority", "From", "To", "SentTime", "ReceivedTime", "TimeInBetweenMs");
            if (Files.exists(Paths.get(csvType.type))) {
                csvFormat = CSVFormat.DEFAULT;
            }
            this.csvPrinter = new CSVPrinter(Files.newBufferedWriter(Paths.get(csvType.type),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE),
                    csvFormat);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            System.exit(-1);
        }
    }

    public void addRecord(Message message) {
        try {
            Header header = message.getHeader();
            JsonObject payload = JsonParser.parseString(new String(message.getPayload(), StandardCharsets.UTF_8)).getAsJsonObject();
            Long sentTime = payload.getAsJsonObject("state").getAsJsonObject("reported").get("sentTime").getAsJsonPrimitive().getAsLong();
            if (csvType == CsvType.SENT) {
                this.csvPrinter.printRecord(header.getPriority(),
                        header.getFrom() == null ? "null" : header.getFrom(),
                        header.getTo() == null ? "null" : header.getTo(),
                        sentTime,
                        "-");
            } else {
                Long receiveTime = LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli();
                Long timeInBetween = receiveTime - sentTime;
                this.csvPrinter.printRecord(header.getPriority(),
                        header.getFrom() == null ? "null" : header.getFrom(),
                        header.getTo() == null ? "null" : header.getTo(),
                        sentTime,
                        receiveTime,
                        timeInBetween);
            }
            this.csvPrinter.flush();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            System.exit(-1);
        }
    }

}
