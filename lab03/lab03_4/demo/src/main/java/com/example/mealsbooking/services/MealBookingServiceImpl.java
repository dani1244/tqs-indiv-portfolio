package com.example.mealsbooking.services;

import com.example.mealsbooking.entity.MealBooking;
import com.example.mealsbooking.repository.MealBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class MealBookingServiceImpl {
    
    @Autowired
    private MealBookingRepository mealBookingRepository;
    
    private static final int DEFAULT_CAPACITY = 100;
    
    public MealBooking bookMeal(String studentId, String serviceShift) {
        validateBookingRequest(studentId, serviceShift);
        
        String shiftKey = serviceShift;
        
        long currentBookings = mealBookingRepository.findByServiceShift(shiftKey)
            .stream()
            .filter(m -> !m.isCancelled())
            .count();
        
        if (currentBookings >= DEFAULT_CAPACITY) {
            throw new IllegalStateException("No available spots for this shift");
        }
        
        String token = UUID.randomUUID().toString().substring(0, 8);
        
        MealBooking booking = new MealBooking(token, studentId, serviceShift);
        return mealBookingRepository.save(booking);
    }
    
    public Optional<MealBooking> getReservation(String token) {
        return mealBookingRepository.findByToken(token);
    }
    
    public boolean verifyReservation(String token) {
        Optional<MealBooking> reservation = mealBookingRepository.findByToken(token);
        return reservation.isPresent() && 
               !reservation.get().isUsed() && 
               !reservation.get().isCancelled();
    }
    
    public boolean checkIn(String token) {
        Optional<MealBooking> reservation = mealBookingRepository.findByToken(token);
        
        if (reservation.isEmpty() || 
            reservation.get().isUsed() || 
            reservation.get().isCancelled()) {
            return false;
        }
        
        MealBooking booking = reservation.get();
        booking.markAsUsed();
        mealBookingRepository.save(booking);
        return true;
    }
    
    public boolean cancelReservation(String token) {
        Optional<MealBooking> reservation = mealBookingRepository.findByToken(token);
        
        if (reservation.isEmpty() || 
            reservation.get().isUsed() || 
            reservation.get().isCancelled()) {
            return false;
        }
        
        MealBooking booking = reservation.get();
        booking.cancel();
        mealBookingRepository.save(booking);
        return true;
    }
    
    public int getAvailableSpots(String shift) {
        long current = mealBookingRepository.findByServiceShift(shift)
            .stream()
            .filter(m -> !m.isCancelled())
            .count();
        int capacity = DEFAULT_CAPACITY;
        return (int) Math.max(0, capacity - current);
    }
    
    public List<MealBooking> getStudentBookings(String studentId) {
        return mealBookingRepository.findByStudentId(studentId);
    }
    
    private void validateBookingRequest(String studentId, String serviceShift) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (serviceShift == null || serviceShift.trim().isEmpty()) {
            throw new IllegalArgumentException("Service shift is required");
        }
        
        boolean hasActiveBooking = mealBookingRepository.findByStudentId(studentId)
            .stream()
            .anyMatch(b -> b.getServiceShift().equals(serviceShift) && 
                          !b.isCancelled());
        
        if (hasActiveBooking) {
            throw new IllegalStateException("Student already has a reservation for this shift");
        }
    }
}
