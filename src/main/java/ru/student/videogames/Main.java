package ru.student.videogames;

import ru.student.videogames.config.DatabaseConfig;
import ru.student.videogames.db.DatabaseConnectionFactory;
import ru.student.videogames.db.DatabaseInitializer;
import ru.student.videogames.model.RawGameRecord;
import ru.student.videogames.parser.GameCsvParser;

import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseConfig config = DatabaseConfig.load();
        GameCsvParser parser = new GameCsvParser();
        List<RawGameRecord> records = parser.parse(config.getCsvPath());

        System.out.println("Video Game Sales Analytics");
        System.out.println("CSV file: " + config.getCsvPath());
        System.out.println("Rows read: " + records.size());
        System.out.println("Rows with missing year: " + countMissingYears(records));
        System.out.println("Rows with unknown publisher: " + countUnknownPublishers(records));
        System.out.println();

        try (Connection connection = new DatabaseConnectionFactory(config.getDatabasePath()).createConnection()) {
            DatabaseInitializer databaseInitializer = new DatabaseInitializer(connection);
            databaseInitializer.initialize();
            System.out.println("Database initialized: " + config.getDatabasePath());
        }
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
