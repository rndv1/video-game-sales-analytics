package ru.student.videogames.dto;

public class PlatformAverageSalesDto {
    private final String platform;
    private final double avgGlobalSales;

    public PlatformAverageSalesDto(String platform, double avgGlobalSales) {
        this.platform = platform;
        this.avgGlobalSales = avgGlobalSales;
    }

    public String getPlatform() {
        return platform;
    }

    public double getAvgGlobalSales() {
        return avgGlobalSales;
    }
}
