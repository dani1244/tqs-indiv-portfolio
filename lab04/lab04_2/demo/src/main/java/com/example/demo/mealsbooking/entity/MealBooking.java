package com.example.demo.mealsbooking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meal_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealBooking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private String serviceShift;
    
    @Column(nullable = false)
    private LocalDateTime reservationTime;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column(nullable = false)
    private boolean cancelled = false;
    
    public MealBooking(String token, String studentId, String serviceShift) {
        this.token = token;
        this.studentId = studentId;
        this.serviceShift = serviceShift;
        this.reservationTime = LocalDateTime.now();
        this.used = false;
        this.cancelled = false;
    }
    
    public void markAsUsed() {
        this.used = true;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}