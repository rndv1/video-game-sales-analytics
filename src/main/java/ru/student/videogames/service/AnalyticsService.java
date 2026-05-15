package ru.student.videogames.service;

import ru.student.videogames.dto.PlatformAverageSalesDto;
import ru.student.videogames.repository.AnalyticsRepository;

import java.sql.SQLException;
import java.util.List;

public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<PlatformAverageSalesDto> getAverageGlobalSalesByPlatform() throws SQLException {
        return analyticsRepository.findAverageGlobalSalesByPlatform();
    }
}
