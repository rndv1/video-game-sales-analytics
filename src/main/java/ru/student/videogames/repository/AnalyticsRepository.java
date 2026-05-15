package ru.student.videogames.repository;

import ru.student.videogames.dto.GameSalesDto;
import ru.student.videogames.dto.PlatformAverageSalesDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnalyticsRepository {
    private final Connection connection;

    public AnalyticsRepository(Connection connection) {
        this.connection = connection;
    }

    public List<PlatformAverageSalesDto> findAverageGlobalSalesByPlatform() throws SQLException {
        String sql = """
                SELECT
                    p.name AS platform,
                    ROUND(AVG(s.global_sales), 2) AS avg_global_sales
                FROM sales s
                JOIN games g ON s.game_id = g.id
                JOIN platforms p ON g.platform_id = p.id
                GROUP BY p.name
                ORDER BY avg_global_sales DESC
                """;

        List<PlatformAverageSalesDto> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new PlatformAverageSalesDto(
                        resultSet.getString("platform"),
                        resultSet.getDouble("avg_global_sales")
                ));
            }
        }
        return result;
    }

    public Optional<GameSalesDto> findTopEuSalesGameByYear(int year) throws SQLException {
        String sql = """
                SELECT
                    g.name,
                    p.name AS platform,
                    g.release_year,
                    ge.name AS genre,
                    pub.name AS publisher,
                    s.eu_sales
                FROM sales s
                JOIN games g ON s.game_id = g.id
                JOIN platforms p ON g.platform_id = p.id
                JOIN genres ge ON g.genre_id = ge.id
                JOIN publishers pub ON g.publisher_id = pub.id
                WHERE g.release_year = ?
                ORDER BY s.eu_sales DESC
                LIMIT 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapGameSales(resultSet, "eu_sales"));
                }
            }
        }
        return Optional.empty();
    }

    private GameSalesDto mapGameSales(ResultSet resultSet, String salesColumn) throws SQLException {
        int releaseYear = resultSet.getInt("release_year");
        Integer year = resultSet.wasNull() ? null : releaseYear;

        return new GameSalesDto(
                resultSet.getString("name"),
                resultSet.getString("platform"),
                year,
                resultSet.getString("genre"),
                resultSet.getString("publisher"),
                resultSet.getDouble(salesColumn)
        );
    }
}
