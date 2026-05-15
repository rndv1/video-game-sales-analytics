package ru.student.videogames.repository;

import ru.student.videogames.model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class GameRepository {
    private final Connection connection;

    public GameRepository(Connection connection) {
        this.connection = connection;
    }

    public int save(Game game) throws SQLException {
        String sql = """
                INSERT INTO games ("rank", name, platform_id, release_year, genre_id, publisher_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, game.getRank());
            statement.setString(2, game.getName());
            statement.setInt(3, game.getPlatformId());
            if (game.getReleaseYear() == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, game.getReleaseYear());
            }
            statement.setInt(5, game.getGenreId());
            statement.setInt(6, game.getPublisherId());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Cannot insert game: " + game.getName());
    }

    public Integer findIdByRank(int rank) throws SQLException {
        String sql = "SELECT id FROM games WHERE \"rank\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rank);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return null;
    }

    public int count() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM games")) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }
}
