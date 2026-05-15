package ru.student.videogames.model;

public class Game {
    private final int id;
    private final int rank;
    private final String name;
    private final int platformId;
    private final Integer releaseYear;
    private final int genreId;
    private final int publisherId;

    public Game(
            int rank,
            String name,
            int platformId,
            Integer releaseYear,
            int genreId,
            int publisherId
    ) {
        this(0, rank, name, platformId, releaseYear, genreId, publisherId);
    }

    public Game(
            int id,
            int rank,
            String name,
            int platformId,
            Integer releaseYear,
            int genreId,
            int publisherId
    ) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.platformId = platformId;
        this.releaseYear = releaseYear;
        this.genreId = genreId;
        this.publisherId = publisherId;
    }

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getPlatformId() {
        return platformId;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public int getGenreId() {
        return genreId;
    }

    public int getPublisherId() {
        return publisherId;
    }
}
