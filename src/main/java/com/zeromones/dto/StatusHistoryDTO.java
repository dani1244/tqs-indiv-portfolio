package com.zeromones.dto;

import com.zeromones.model.RequestStatus;
import com.zeromones.model.StatusHistory;
import java.time.LocalDateTime;

public class StatusHistoryDTO {
    
    private RequestStatus status;
    private LocalDateTime timestamp;
    private String notes;
    
    public StatusHistoryDTO(StatusHistory history) {
        this.status = history.getStatus();
        this.timestamp = history.getTimestamp();
        this.notes = history.getNotes();
    }
    
    public StatusHistoryDTO() {
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
