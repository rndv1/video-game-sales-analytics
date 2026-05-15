package ru.student.videogames.repository;

import ru.student.videogames.dto.PlatformAverageSalesDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
