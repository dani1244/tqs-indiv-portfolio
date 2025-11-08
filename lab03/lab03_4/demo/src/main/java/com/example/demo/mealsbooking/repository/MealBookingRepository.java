package com.example.mealsbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mealsbooking.entity.MealBooking;

@Repository
public interface MealBookingRepository extends JpaRepository<MealBooking, Long> {
    
    Optional<MealBooking> findByToken(String token);
    
    List<MealBooking> findByStudentId(String studentId);
    
    List<MealBooking> findByServiceShift(String serviceShift);
}