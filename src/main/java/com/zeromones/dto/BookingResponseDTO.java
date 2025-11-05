package com.zeromones.dto;

import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import com.zeromones.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponseDTO {
    
    private Long id;
    private String accessToken;
    private String municipality;
    private String itemDescription;
    private LocalDate collectionDate;
    private TimeSlot timeSlot;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private RequestStatus currentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer numberOfItems;
    private List<StatusHistoryDTO> statusHistory;
    
    public BookingResponseDTO(ServiceRequest request) {
        this.id = request.getId();
        this.accessToken = request.getAccessToken();
        this.municipality = request.getMunicipality();
        this.itemDescription = request.getItemDescription();
        this.collectionDate = request.getCollectionDate();
        this.timeSlot = request.getTimeSlot();
        this.address = request.getAddress();
        this.contactEmail = request.getContactEmail();
        this.contactPhone = request.getContactPhone();
        this.currentStatus = request.getCurrentStatus();
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();
        this.numberOfItems = request.getNumberOfItems();
        this.statusHistory = request.getStatusHistory().stream()
            .map(StatusHistoryDTO::new)
            .collect(Collectors.toList());
    }
    
    public BookingResponseDTO() {
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
    
    public Integer getNumberOfItems() {
        return numberOfItems;
    }
    
    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
    
    public List<StatusHistoryDTO> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistoryDTO> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
