package com.zeromones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks status changes for a service request
 */
@Entity
@Table(name = "status_history")
public class StatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;
    
    public StatusHistory() {
        this.timestamp = LocalDateTime.now();
    }
    
    public StatusHistory(RequestStatus status, String notes) {
        this();
        this.status = status;
        this.notes = notes;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
    
    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }
}
