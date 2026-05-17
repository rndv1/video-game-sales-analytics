package ru.student.videogames.dto;

public class GameSalesDto {
    private final String name;
    private final String platform;
    private final Integer releaseYear;
    private final String genre;
    private final String publisher;
    private final double sales;

    public GameSalesDto(
            String name,
            String platform,
            Integer releaseYear,
            String genre,
            String publisher,
            double sales
    ) {
        this.name = name;
        this.platform = platform;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.publisher = publisher;
        this.sales = sales;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public double getSales() {
        return sales;
    }
}
