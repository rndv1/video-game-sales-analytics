package ru.student.videogames.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ru.student.videogames.model.RawGameRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameCsvParser {
    public List<RawGameRecord> parse(Path csvPath) throws IOException {
        List<RawGameRecord> records = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        try (Reader reader = newUtf8ReaderWithoutBom(csvPath);
             CSVParser parser = format.parse(reader)) {
            for (CSVRecord record : parser) {
                records.add(toRawGameRecord(record));
            }
        }

        return records;
    }

    private Reader newUtf8ReaderWithoutBom(Path csvPath) throws IOException {
        InputStream inputStream = java.nio.file.Files.newInputStream(csvPath);
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 3);
        byte[] bom = new byte[3];
        int bytesRead = pushbackInputStream.read(bom, 0, bom.length);

        boolean hasUtf8Bom = bytesRead == 3
                && (bom[0] & 0xFF) == 0xEF
                && (bom[1] & 0xFF) == 0xBB
                && (bom[2] & 0xFF) == 0xBF;

        if (!hasUtf8Bom && bytesRead > 0) {
            pushbackInputStream.unread(bom, 0, bytesRead);
        }

        return new InputStreamReader(pushbackInputStream, StandardCharsets.UTF_8);
    }

    private RawGameRecord toRawGameRecord(CSVRecord record) {
        try {
            return new RawGameRecord(
                    parseInt(required(record, "Rank")),
                    required(record, "Name"),
                    required(record, "Platform"),
                    parseYear(required(record, "Year")),
                    required(record, "Genre"),
                    required(record, "Publisher"),
                    parseDouble(required(record, "NA_Sales")),
                    parseDouble(required(record, "EU_Sales")),
                    parseDouble(required(record, "JP_Sales")),
                    parseDouble(required(record, "Other_Sales")),
                    parseDouble(required(record, "Global_Sales"))
            );
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Cannot parse CSV record #" + record.getRecordNumber(), exception);
        }
    }

    private String required(CSVRecord record, String column) {
        String value = value(record, column);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Column " + column + " is empty");
        }
        return value;
    }

    private String value(CSVRecord record, String column) {
        String value = record.get(column);
        if (value == null) {
            return "";
        }
        return value.replace("\uFEFF", "").trim();
    }

    private Integer parseYear(String value) {
        double yearAsDouble = Double.parseDouble(value);
        return (int) yearAsDouble;
    }

    private int parseInt(String value) {
        return Integer.parseInt(value);
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value);
    }
}
