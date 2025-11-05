package com.zeromones.model;

public enum WorkListStatus {
    PENDING("Work list created, awaiting assignment"),
    IN_PROGRESS("Work in progress"),
    COMPLETED("All work completed"),
    CANCELLED("Work list cancelled");

    private final String description;

    WorkListStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
