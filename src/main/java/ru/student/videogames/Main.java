package ru.student.videogames;

import ru.student.videogames.model.RawGameRecord;
import ru.student.videogames.parser.GameCsvParser;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Path csvPath = Path.of("data/games.csv");
        GameCsvParser parser = new GameCsvParser();
        List<RawGameRecord> records = parser.parse(csvPath);

        System.out.println("Video Game Sales Analytics");
        System.out.println("CSV file: " + csvPath);
        System.out.println("Rows read: " + records.size());
        System.out.println("Rows with missing year: " + countMissingYears(records));
        System.out.println("Rows with unknown publisher: " + countUnknownPublishers(records));
    }

    private static long countMissingYears(List<RawGameRecord> records) {
        return records.stream()
                .filter(record -> record.getYear() == null)
                .count();
    }

    private static long countUnknownPublishers(List<RawGameRecord> records) {
        return records.stream()
                .filter(record -> "Unknown".equals(record.getPublisher()))
                .count();
    }
}
