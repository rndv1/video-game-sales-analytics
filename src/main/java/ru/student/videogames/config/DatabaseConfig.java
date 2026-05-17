package ru.student.videogames.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DEFAULT_CSV_PATH = "data/games.csv";
    private static final String DEFAULT_DATABASE_PATH = "database/video_games.db";
    private static final String DEFAULT_CHART_PATH = "output/charts/avg_global_sales_by_platform.png";
    private static final int DEFAULT_TOP_PLATFORMS = 15;

    private final Path csvPath;
    private final Path databasePath;
    private final Path chartPath;
    private final int chartTopPlatforms;

    public DatabaseConfig(Path csvPath, Path databasePath, Path chartPath, int chartTopPlatforms) {
        this.csvPath = csvPath;
        this.databasePath = databasePath;
        this.chartPath = chartPath;
        this.chartTopPlatforms = chartTopPlatforms;
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
        Path chartPath = Paths.get(properties.getProperty("chart.path", DEFAULT_CHART_PATH));
        int chartTopPlatforms = parseInt(properties.getProperty("chart.top-platforms"), DEFAULT_TOP_PLATFORMS);

        return new DatabaseConfig(csvPath, databasePath, chartPath, chartTopPlatforms);
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public Path getCsvPath() {
        return csvPath;
    }

    public Path getDatabasePath() {
        return databasePath;
    }

    public Path getChartPath() {
        return chartPath;
    }

    public int getChartTopPlatforms() {
        return chartTopPlatforms;
    }
}
