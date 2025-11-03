package com.zeromones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service_requests")
public class ServiceRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 36)
    private String accessToken;
    
    @NotBlank(message = "Municipality is required")
    @Column(nullable = false)
    private String municipality;
    
    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String itemDescription;
    
    @NotNull(message = "Collection date is required")
    @Future(message = "Collection date must be in the future")
    @Column(nullable = false)
    private LocalDate collectionDate;
    
    @NotNull(message = "Time slot is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlot timeSlot;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 300)
    private String address;
    
    @Email(message = "Invalid email format")
    @Column(length = 100)
    private String contactEmail;
    
    @Pattern(regexp = "^[0-9]{9}$", message = "Phone must be 9 digits")
    @Column(length = 15)
    private String contactPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus currentStatus;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp ASC")
    private List<StatusHistory> statusHistory = new ArrayList<>();
    
    @Min(value = 1, message = "Number of items must be at least 1")
    @Max(value = 5, message = "Cannot exceed 5 items per request")
    @Column(nullable = false)
    private Integer numberOfItems = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    public ServiceRequest() {
        this.accessToken = UUID.randomUUID().toString();
        this.currentStatus = RequestStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        addStatusHistory(RequestStatus.RECEIVED, "Request created");
    }
    
    public void updateStatus(RequestStatus newStatus, String notes) {
        if (!this.currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", 
                    this.currentStatus, newStatus)
            );
        }
        this.currentStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
        addStatusHistory(newStatus, notes);
    }
    
    private void addStatusHistory(RequestStatus status, String notes) {
        StatusHistory history = new StatusHistory(status, notes);
        history.setServiceRequest(this);
        this.statusHistory.add(history);
    }
    
    public boolean isCancellable() {
        return currentStatus == RequestStatus.RECEIVED || 
               currentStatus == RequestStatus.ASSIGNED;
    }
    
    public boolean isModifiable() {
        return currentStatus == RequestStatus.RECEIVED;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getMunicipality() {
        return municipality;
    }
    
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
    
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    
    public LocalDate getCollectionDate() {
        return collectionDate;
    }
    
    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }
    
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }
    
    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public RequestStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(RequestStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
        this.updatedAt = LocalDateTime.now();
    }
}
