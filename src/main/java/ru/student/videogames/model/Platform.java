package ru.student.videogames.model;

public class Platform {
    private final int id;
    private final String name;

    public Platform(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
