package ru.student.videogames;

import ru.student.videogames.config.DatabaseConfig;
import ru.student.videogames.db.DatabaseConnectionFactory;
import ru.student.videogames.db.DatabaseInitializer;
import ru.student.videogames.parser.GameCsvParser;
import ru.student.videogames.repository.GameRepository;
import ru.student.videogames.repository.GenreRepository;
import ru.student.videogames.repository.PlatformRepository;
import ru.student.videogames.repository.PublisherRepository;
import ru.student.videogames.repository.SalesRepository;
import ru.student.videogames.service.ImportService;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseConfig config = DatabaseConfig.load();

        System.out.println("Video Game Sales Analytics");
        System.out.println("CSV file: " + config.getCsvPath());
        System.out.println("Database: " + config.getDatabasePath());
        System.out.println();

        try (Connection connection = new DatabaseConnectionFactory(config.getDatabasePath()).createConnection()) {
            DatabaseInitializer databaseInitializer = new DatabaseInitializer(connection);
            databaseInitializer.initialize();
            System.out.println("Database initialized: " + config.getDatabasePath());

            ImportService.ImportSummary summary = createImportService(connection).importFromCsv(config.getCsvPath());
            printImportSummary(summary);
        }
    }

    private static ImportService createImportService(Connection connection) {
        return new ImportService(
                connection,
                new GameCsvParser(),
                new PlatformRepository(connection),
                new GenreRepository(connection),
                new PublisherRepository(connection),
                new GameRepository(connection),
                new SalesRepository(connection)
        );
    }

    private static void printImportSummary(ImportService.ImportSummary summary) {
        System.out.println();
        System.out.println("Import summary");
        System.out.println("Rows read: " + summary.getRowsRead());
        System.out.println("Games saved: " + summary.getGamesSaved());
        System.out.println("Duplicate games skipped: " + summary.getDuplicateGamesSkipped());
        System.out.println("Platforms found: " + summary.getPlatformsFound());
        System.out.println("Genres found: " + summary.getGenresFound());
        System.out.println("Publishers found: " + summary.getPublishersFound());
    }
}
