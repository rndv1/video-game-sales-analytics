package ru.student.videogames.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DEFAULT_CSV_PATH = "data/games.csv";
    private static final String DEFAULT_DATABASE_PATH = "database/video_games.db";

    private final Path csvPath;
    private final Path databasePath;

    public DatabaseConfig(Path csvPath, Path databasePath) {
        this.csvPath = csvPath;
        this.databasePath = databasePath;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();

        try (InputStream inputStream = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read application.properties", exception);
        }

        Path csvPath = Paths.get(properties.getProperty("csv.path", DEFAULT_CSV_PATH));
        Path databasePath = Paths.get(properties.getProperty("database.path", DEFAULT_DATABASE_PATH));

        return new DatabaseConfig(csvPath, databasePath);
    }

    public Path getCsvPath() {
        return csvPath;
    }

    public Path getDatabasePath() {
        return databasePath;
    }
}
