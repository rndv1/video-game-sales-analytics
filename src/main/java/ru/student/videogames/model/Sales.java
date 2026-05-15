package ru.student.videogames.model;

public class Sales {
    private final int id;
    private final int gameId;
    private final double naSales;
    private final double euSales;
    private final double jpSales;
    private final double otherSales;
    private final double globalSales;

    public Sales(
            int gameId,
            double naSales,
            double euSales,
            double jpSales,
            double otherSales,
            double globalSales
    ) {
        this(0, gameId, naSales, euSales, jpSales, otherSales, globalSales);
    }

    public Sales(
            int id,
            int gameId,
            double naSales,
            double euSales,
            double jpSales,
            double otherSales,
            double globalSales
    ) {
        this.id = id;
        this.gameId = gameId;
        this.naSales = naSales;
        this.euSales = euSales;
        this.jpSales = jpSales;
        this.otherSales = otherSales;
        this.globalSales = globalSales;
    }

    public int getId() {
        return id;
    }

    public int getGameId() {
        return gameId;
    }

    public double getNaSales() {
        return naSales;
    }

    public double getEuSales() {
        return euSales;
    }

    public double getJpSales() {
        return jpSales;
    }

    public double getOtherSales() {
        return otherSales;
    }

    public double getGlobalSales() {
        return globalSales;
    }
}
