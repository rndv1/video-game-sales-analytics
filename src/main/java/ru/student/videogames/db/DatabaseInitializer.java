package ru.student.videogames.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS platforms (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS genres (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS publishers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS games (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        "rank" INTEGER NOT NULL UNIQUE,
                        name TEXT NOT NULL,
                        platform_id INTEGER NOT NULL,
                        release_year INTEGER,
                        genre_id INTEGER NOT NULL,
                        publisher_id INTEGER NOT NULL,

                        FOREIGN KEY (platform_id) REFERENCES platforms(id),
                        FOREIGN KEY (genre_id) REFERENCES genres(id),
                        FOREIGN KEY (publisher_id) REFERENCES publishers(id)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS sales (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        game_id INTEGER NOT NULL UNIQUE,
                        na_sales REAL NOT NULL,
                        eu_sales REAL NOT NULL,
                        jp_sales REAL NOT NULL,
                        other_sales REAL NOT NULL,
                        global_sales REAL NOT NULL,

                        FOREIGN KEY (game_id) REFERENCES games(id)
                    )
                    """);
        }
    }

    public void clearData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM sales");
            statement.executeUpdate("DELETE FROM games");
            statement.executeUpdate("DELETE FROM platforms");
            statement.executeUpdate("DELETE FROM genres");
            statement.executeUpdate("DELETE FROM publishers");
            statement.executeUpdate("""
                    DELETE FROM sqlite_sequence
                    WHERE name IN ('sales', 'games', 'platforms', 'genres', 'publishers')
                    """);
        }
    }
}
