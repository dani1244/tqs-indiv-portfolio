package com.zeromones.model;

/**
 * Represents the time slot for waste collection
 */
public enum TimeSlot {
    MORNING("08:00-12:00"),
    AFTERNOON("12:00-17:00"),
    EVENING("17:00-20:00");
    
    private final String timeRange;
    
    TimeSlot(String timeRange) {
        this.timeRange = timeRange;
    }
    
    public String getTimeRange() {
        return timeRange;
    }
    
    @Override
    public String toString() {
        return this.name() + " (" + timeRange + ")";
    }
}
