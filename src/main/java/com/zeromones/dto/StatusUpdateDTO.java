package com.zeromones.dto;

import com.zeromones.model.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDTO {
    
    @NotNull(message = "Status is required")
    private RequestStatus status;
    
    private String notes;
    
    public StatusUpdateDTO() {
    }
    
    public StatusUpdateDTO(RequestStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
