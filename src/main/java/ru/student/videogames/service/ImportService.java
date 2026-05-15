package ru.student.videogames.service;

import ru.student.videogames.model.Game;
import ru.student.videogames.model.RawGameRecord;
import ru.student.videogames.model.Sales;
import ru.student.videogames.parser.GameCsvParser;
import ru.student.videogames.repository.GameRepository;
import ru.student.videogames.repository.GenreRepository;
import ru.student.videogames.repository.PlatformRepository;
import ru.student.videogames.repository.PublisherRepository;
import ru.student.videogames.repository.SalesRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ImportService {
    private final Connection connection;
    private final GameCsvParser parser;
    private final PlatformRepository platformRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;
    private final GameRepository gameRepository;
    private final SalesRepository salesRepository;

    public ImportService(
            Connection connection,
            GameCsvParser parser,
            PlatformRepository platformRepository,
            GenreRepository genreRepository,
            PublisherRepository publisherRepository,
            GameRepository gameRepository,
            SalesRepository salesRepository
    ) {
        this.connection = connection;
        this.parser = parser;
        this.platformRepository = platformRepository;
        this.genreRepository = genreRepository;
        this.publisherRepository = publisherRepository;
        this.gameRepository = gameRepository;
        this.salesRepository = salesRepository;
    }

    public ImportSummary importFromCsv(Path csvPath) throws IOException, SQLException {
        List<RawGameRecord> records = parser.parse(csvPath);
        boolean previousAutoCommit = connection.getAutoCommit();
        int savedGames = 0;

        try {
            connection.setAutoCommit(false);

            for (RawGameRecord record : records) {
                int platformId = platformRepository.findOrCreate(record.getPlatform());
                int genreId = genreRepository.findOrCreate(record.getGenre());
                int publisherId = publisherRepository.findOrCreate(record.getPublisher());

                Game game = new Game(
                        record.getRank(),
                        record.getName(),
                        platformId,
                        record.getYear(),
                        genreId,
                        publisherId
                );
                int gameId = gameRepository.save(game);

                Sales sales = new Sales(
                        gameId,
                        record.getNaSales(),
                        record.getEuSales(),
                        record.getJpSales(),
                        record.getOtherSales(),
                        record.getGlobalSales()
                );
                salesRepository.save(sales);
                savedGames++;
            }

            connection.commit();
        } catch (SQLException | RuntimeException exception) {
            rollbackQuietly();
            throw exception;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }

        return new ImportSummary(
                records.size(),
                savedGames,
                platformRepository.count(),
                genreRepository.count(),
                publisherRepository.count()
        );
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // The original import exception is more useful for the caller.
        }
    }

    public static class ImportSummary {
        private final int rowsRead;
        private final int gamesSaved;
        private final int platformsFound;
        private final int genresFound;
        private final int publishersFound;

        public ImportSummary(int rowsRead, int gamesSaved, int platformsFound, int genresFound, int publishersFound) {
            this.rowsRead = rowsRead;
            this.gamesSaved = gamesSaved;
            this.platformsFound = platformsFound;
            this.genresFound = genresFound;
            this.publishersFound = publishersFound;
        }

        public int getRowsRead() {
            return rowsRead;
        }

        public int getGamesSaved() {
            return gamesSaved;
        }

        public int getPlatformsFound() {
            return platformsFound;
        }

        public int getGenresFound() {
            return genresFound;
        }

        public int getPublishersFound() {
            return publishersFound;
        }
    }
}
