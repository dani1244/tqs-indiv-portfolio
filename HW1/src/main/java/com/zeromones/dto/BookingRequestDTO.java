package com.zeromones.dto;

import com.zeromones.model.TimeSlot;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class BookingRequestDTO {
    
    @NotBlank(message = "Municipality is required")
    private String municipality;
    
    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String itemDescription;
    
    @NotNull(message = "Collection date is required")
    @Future(message = "Collection date must be in the future")
    private LocalDate collectionDate;
    
    @NotNull(message = "Time slot is required")
    private TimeSlot timeSlot;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @Pattern(regexp = "^[0-9]{9}$", message = "Phone must be 9 digits")
    private String contactPhone;
    
    @Min(value = 1, message = "Number of items must be at least 1")
    @Max(value = 5, message = "Cannot exceed 5 items per request")
    private Integer numberOfItems = 1;
    
    public BookingRequestDTO() {
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
    
    public Integer getNumberOfItems() {
        return numberOfItems;
    }
    
    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}
