package ru.student.videogames.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.student.videogames.db.DatabaseConnectionFactory;
import ru.student.videogames.db.DatabaseInitializer;
import ru.student.videogames.dto.GameSalesDto;
import ru.student.videogames.dto.PlatformAverageSalesDto;
import ru.student.videogames.repository.AnalyticsRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalyticsServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void analyticsQueriesReturnExpectedGamesAndPlatformAverages() throws Exception {
        Path databasePath = tempDir.resolve("video_games_test.db");

        try (Connection connection = new DatabaseConnectionFactory(databasePath).createConnection()) {
            new DatabaseInitializer(connection).initialize();
            insertTestData(connection);

            AnalyticsService service = new AnalyticsService(new AnalyticsRepository(connection));

            List<PlatformAverageSalesDto> averages = service.getAverageGlobalSalesByPlatform();
            assertEquals("Wii", averages.get(0).getPlatform());
            assertEquals(82.74, averages.get(0).getAvgGlobalSales());

            Optional<GameSalesDto> topEu = service.findTopEuSalesGame(2000);
            assertTrue(topEu.isPresent());
            assertEquals("Driver 2", topEu.get().getName());
            assertEquals(2.10, topEu.get().getSales());

            Optional<GameSalesDto> topJpSports = service.findTopJpSportsGame(2000, 2006);
            assertTrue(topJpSports.isPresent());
            assertEquals("Wii Sports", topJpSports.get().getName());
            assertEquals(3.77, topJpSports.get().getSales());
        }
    }

    private void insertTestData(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO platforms (id, name) VALUES (1, 'PS'), (2, 'Wii')");
            statement.executeUpdate("INSERT INTO genres (id, name) VALUES (1, 'Action'), (2, 'Sports')");
            statement.executeUpdate("INSERT INTO publishers (id, name) VALUES (1, 'Atari'), (2, 'Nintendo')");
            statement.executeUpdate("""
                    INSERT INTO games (id, "rank", name, platform_id, release_year, genre_id, publisher_id)
                    VALUES
                        (1, 100, 'Driver 2', 1, 2000, 1, 1),
                        (2, 1, 'Wii Sports', 2, 2006, 2, 2),
                        (3, 200, 'Other Sports', 1, 2002, 2, 1)
                    """);
            statement.executeUpdate("""
                    INSERT INTO sales (game_id, na_sales, eu_sales, jp_sales, other_sales, global_sales)
                    VALUES
                        (1, 1.80, 2.10, 0.02, 0.30, 4.22),
                        (2, 41.49, 29.02, 3.77, 8.46, 82.74),
                        (3, 0.10, 0.20, 1.20, 0.01, 1.51)
                    """);
        }
    }
}
