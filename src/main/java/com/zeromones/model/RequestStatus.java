package com.zeromones.model;

/**
 * Represents the status of a service request
 */
public enum RequestStatus {
    RECEIVED("Request received and pending assignment"),
    ASSIGNED("Request assigned to collection team"),
    IN_PROGRESS("Collection in progress"),
    COMPLETED("Collection completed successfully"),
    CANCELLED("Request cancelled by user or system");
    
    private final String description;
    
    RequestStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this status can transition to the target status
     */
    public boolean canTransitionTo(RequestStatus target) {
        return switch (this) {
            case RECEIVED -> target == ASSIGNED || target == CANCELLED;
            case ASSIGNED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
