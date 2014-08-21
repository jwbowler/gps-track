package edu.mit.zbt.rushgps.app;


public class CarInfo {
    private final String id;
    private final String description;

    public CarInfo(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

}