package ru.student.videogames.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionFactory {
    private final Path databasePath;

    public DatabaseConnectionFactory(Path databasePath) {
        this.databasePath = databasePath;
    }

    public Connection createConnection() throws SQLException, IOException {
        Path parent = databasePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        loadSqliteDriver();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        enableForeignKeys(connection);
        return connection;
    }

    private void loadSqliteDriver() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("SQLite JDBC driver not found", exception);
        }
    }

    private void enableForeignKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }
}
