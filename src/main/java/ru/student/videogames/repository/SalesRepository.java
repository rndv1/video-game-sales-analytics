package ru.student.videogames.repository;

import ru.student.videogames.model.Sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SalesRepository {
    private final Connection connection;

    public SalesRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Sales sales) throws SQLException {
        String sql = """
                INSERT INTO sales (game_id, na_sales, eu_sales, jp_sales, other_sales, global_sales)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sales.getGameId());
            statement.setDouble(2, sales.getNaSales());
            statement.setDouble(3, sales.getEuSales());
            statement.setDouble(4, sales.getJpSales());
            statement.setDouble(5, sales.getOtherSales());
            statement.setDouble(6, sales.getGlobalSales());
            statement.executeUpdate();
        }
    }

    public int count() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM sales")) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }
}
