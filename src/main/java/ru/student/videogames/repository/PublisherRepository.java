package ru.student.videogames.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PublisherRepository {
    private final Connection connection;
    private final Map<String, Integer> cache = new HashMap<>();

    public PublisherRepository(Connection connection) {
        this.connection = connection;
    }

    public int findOrCreate(String name) throws SQLException {
        String normalizedName = normalize(name);
        Integer cachedId = cache.get(normalizedName);
        if (cachedId != null) {
            return cachedId;
        }

        Integer existingId = findIdByName(normalizedName);
        if (existingId != null) {
            cache.put(normalizedName, existingId);
            return existingId;
        }

        int id = insert(normalizedName);
        cache.put(normalizedName, id);
        return id;
    }

    public int count() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM publishers")) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private Integer findIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM publishers WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return null;
    }

    private int insert(String name) throws SQLException {
        String sql = "INSERT INTO publishers (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Cannot insert publisher: " + name);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank() || "N/A".equalsIgnoreCase(value.trim())) {
            return "Unknown";
        }
        return value.trim();
    }
}
