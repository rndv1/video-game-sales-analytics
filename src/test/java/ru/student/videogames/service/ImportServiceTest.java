package ru.student.videogames.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.student.videogames.db.DatabaseConnectionFactory;
import ru.student.videogames.db.DatabaseInitializer;
import ru.student.videogames.parser.GameCsvParser;
import ru.student.videogames.repository.GameRepository;
import ru.student.videogames.repository.GenreRepository;
import ru.student.videogames.repository.PlatformRepository;
import ru.student.videogames.repository.PublisherRepository;
import ru.student.videogames.repository.SalesRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void importFromCsvSavesNormalizedRecordsAndSkipsDuplicatesOnSecondRun() throws Exception {
        Path csv = tempDir.resolve("games.csv");
        Files.writeString(csv, """
                Rank,Name,Platform,Year,Genre,Publisher,NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales
                1,Wii Sports,Wii,2006,Sports,Nintendo,41.49,29.02,3.77,8.46,82.74
                2,Second Sports,Wii,2005,Sports,,1.00,2.00,0.10,0.20,3.30
                """);

        try (Connection connection = new DatabaseConnectionFactory(tempDir.resolve("test.db")).createConnection()) {
            new DatabaseInitializer(connection).initialize();
            ImportService importService = createImportService(connection);

            ImportService.ImportSummary firstRun = importService.importFromCsv(csv);

            assertEquals(2, firstRun.getRowsRead());
            assertEquals(2, firstRun.getGamesSaved());
            assertEquals(0, firstRun.getDuplicateGamesSkipped());
            assertEquals(1, firstRun.getPlatformsFound());
            assertEquals(1, firstRun.getGenresFound());
            assertEquals(2, firstRun.getPublishersFound());

            assertEquals(2, count(connection, "games"));
            assertEquals(2, count(connection, "sales"));
            assertEquals(1, count(connection, "platforms"));
            assertEquals(1, count(connection, "genres"));
            assertEquals(2, count(connection, "publishers"));

            ImportService.ImportSummary secondRun = importService.importFromCsv(csv);

            assertEquals(0, secondRun.getGamesSaved());
            assertEquals(2, secondRun.getDuplicateGamesSkipped());
            assertEquals(2, count(connection, "games"));
            assertEquals(2, count(connection, "sales"));
        }
    }

    private ImportService createImportService(Connection connection) {
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

    private int count(Connection connection, String table) throws Exception {
        try (var statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }
}
