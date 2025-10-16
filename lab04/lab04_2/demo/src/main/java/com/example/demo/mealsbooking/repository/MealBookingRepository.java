package com.example.demo.mealsbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.mealsbooking.entity.MealBooking;

@Repository
public interface MealBookingRepository extends JpaRepository<MealBooking, Long> {
    
    Optional<MealBooking> findByToken(String token);
    
    List<MealBooking> findByStudentId(String studentId);
    
    List<MealBooking> findByServiceShift(String serviceShift);

    @Query("SELECT m FROM MealBooking m WHERE m.studentId = :studentId AND m.cancelled = false")
    List<MealBooking> findActiveBookingsByStudent(@Param("studentId") String studentId);
}