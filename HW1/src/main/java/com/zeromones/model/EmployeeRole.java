package com.zeromones.model;

public enum EmployeeRole {
    DRIVER("Driver - operates collection vehicle"),
    COLLECTOR("Collector - loads items onto vehicle"),
    SUPERVISOR("Supervisor - manages team and routes"),
    COORDINATOR("Coordinator - plans and assigns work");

    private final String description;

    EmployeeRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
