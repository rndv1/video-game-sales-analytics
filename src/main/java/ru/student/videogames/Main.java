package ru.student.videogames;

import ru.student.videogames.chart.ChartGenerator;
import ru.student.videogames.config.DatabaseConfig;
import ru.student.videogames.db.DatabaseConnectionFactory;
import ru.student.videogames.db.DatabaseInitializer;
import ru.student.videogames.dto.GameSalesDto;
import ru.student.videogames.dto.PlatformAverageSalesDto;
import ru.student.videogames.parser.GameCsvParser;
import ru.student.videogames.repository.AnalyticsRepository;
import ru.student.videogames.repository.GameRepository;
import ru.student.videogames.repository.GenreRepository;
import ru.student.videogames.repository.PlatformRepository;
import ru.student.videogames.repository.PublisherRepository;
import ru.student.videogames.repository.SalesRepository;
import ru.student.videogames.service.AnalyticsService;
import ru.student.videogames.service.ImportService;
import ru.student.videogames.util.ConsoleTablePrinter;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final String SECTION = "-".repeat(60);

    public static void main(String[] args) throws Exception {
        DatabaseConfig config = DatabaseConfig.load();

        System.out.println("Video Game Sales Analytics");
        System.out.println("CSV file: " + config.getCsvPath());
        System.out.println("Database: " + config.getDatabasePath());
        System.out.println();

        try (Connection connection = new DatabaseConnectionFactory(config.getDatabasePath()).createConnection()) {
            DatabaseInitializer databaseInitializer = new DatabaseInitializer(connection);
            databaseInitializer.initialize();
            System.out.println("Database initialized: " + config.getDatabasePath());

            ImportService.ImportSummary summary = createImportService(connection).importFromCsv(config.getCsvPath());
            printImportSummary(summary);

            AnalyticsService analyticsService = new AnalyticsService(new AnalyticsRepository(connection));
            ChartGenerator chartGenerator = new ChartGenerator();
            ConsoleTablePrinter printer = new ConsoleTablePrinter();
            runAnalytics(config, analyticsService, chartGenerator, printer);
        }
    }

    private static ImportService createImportService(Connection connection) {
        return new ImportService(
                connection,
                new GameCsvParser(),
                new PlatformRepository(connection),
                new GenreRepository(connection),
                new PublisherRepository(connection),
                new GameRepository(connection),
                new SalesRepository(connection)
        );
    }

    private static void printImportSummary(ImportService.ImportSummary summary) {
        System.out.println();
        System.out.println("Import summary");
        System.out.println("Rows read: " + summary.getRowsRead());
        System.out.println("Games saved: " + summary.getGamesSaved());
        System.out.println("Duplicate games skipped: " + summary.getDuplicateGamesSkipped());
        System.out.println("Platforms found: " + summary.getPlatformsFound());
        System.out.println("Genres found: " + summary.getGenresFound());
        System.out.println("Publishers found: " + summary.getPublishersFound());
    }

    private static void runAnalytics(
            DatabaseConfig config,
            AnalyticsService analyticsService,
            ChartGenerator chartGenerator,
            ConsoleTablePrinter printer
    ) throws Exception {
        System.out.println();
        System.out.println("Analytics results");

        printAverageSalesByPlatform(config, analyticsService, chartGenerator, printer);
        printTopEuSalesGame(analyticsService, printer);
        printTopJpSportsGame(analyticsService, printer);
    }

    private static void printAverageSalesByPlatform(
            DatabaseConfig config,
            AnalyticsService analyticsService,
            ChartGenerator chartGenerator,
            ConsoleTablePrinter printer
    ) throws Exception {
        System.out.println();
        System.out.println(SECTION);
        System.out.println("Query 1. Average global sales by platform");
        System.out.println(SECTION);
        List<PlatformAverageSalesDto> averages = analyticsService.getAverageGlobalSalesByPlatform();
        printer.printPlatformAverages(averages);
        chartGenerator.saveAverageGlobalSalesByPlatformChart(
                averages,
                config.getChartPath(),
                config.getChartTopPlatforms()
        );
        System.out.println("Chart saved to: " + config.getChartPath());
    }

    private static void printTopEuSalesGame(
            AnalyticsService analyticsService,
            ConsoleTablePrinter printer
    ) throws Exception {
        System.out.println();
        System.out.println(SECTION);
        System.out.println("Query 2. Top EU sales game in 2000");
        System.out.println(SECTION);
        Optional<GameSalesDto> topEuSales = analyticsService.findTopEuSalesGame(2000);
        topEuSales.ifPresentOrElse(
                game -> printer.printGameSales(game, "EU Sales"),
                () -> System.out.println("No data found for year 2000.")
        );
    }

    private static void printTopJpSportsGame(
            AnalyticsService analyticsService,
            ConsoleTablePrinter printer
    ) throws Exception {
        System.out.println();
        System.out.println(SECTION);
        System.out.println("Query 3. Top JP sales sports game from 2000 to 2006");
        System.out.println(SECTION);
        Optional<GameSalesDto> topJpSports = analyticsService.findTopJpSportsGame(2000, 2006);
        topJpSports.ifPresentOrElse(
                game -> printer.printGameSales(game, "JP Sales"),
                () -> System.out.println("No sports game found for 2000-2006.")
        );
    }
}
