package ru.student.videogames.service;

import ru.student.videogames.dto.GameSalesDto;
import ru.student.videogames.dto.PlatformAverageSalesDto;
import ru.student.videogames.repository.AnalyticsRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<PlatformAverageSalesDto> getAverageGlobalSalesByPlatform() throws SQLException {
        return analyticsRepository.findAverageGlobalSalesByPlatform();
    }

    public Optional<GameSalesDto> findTopEuSalesGame(int year) throws SQLException {
        return analyticsRepository.findTopEuSalesGameByYear(year);
    }

    public Optional<GameSalesDto> findTopJpSportsGame(int startYear, int endYear) throws SQLException {
        return analyticsRepository.findTopJpSportsGame(startYear, endYear);
    }
}
