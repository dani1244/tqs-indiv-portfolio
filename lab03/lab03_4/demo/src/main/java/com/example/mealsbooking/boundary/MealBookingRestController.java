package com.example.mealsbooking.boundary;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mealsbooking.entity.MealBooking;
import com.example.mealsbooking.services.MealBookingServiceImpl;

@RestController
@RequestMapping("/bookings")
public class MealBookingRestController {
    
    @Autowired
    private MealBookingServiceImpl mealBookingService;
    
    @PostMapping
    public ResponseEntity<?> bookMeal(
            @RequestParam String studentId,
            @RequestParam String serviceShift) {
        try {
            MealBooking booking = mealBookingService.bookMeal(studentId, serviceShift);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{token}")
    public ResponseEntity<?> getBooking(@PathVariable String token) {
        var booking = mealBookingService.getReservation(token);
        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Booking not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @PatchMapping("/{token}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable String token) {
        if (mealBookingService.checkIn(token)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Check-in successful");
            return ResponseEntity.ok(response);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Cannot check-in: already used, cancelled, or not found");
        return ResponseEntity.badRequest().body(error);
    }
    
    @PatchMapping("/{token}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String token) {
        if (mealBookingService.cancelReservation(token)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reservation cancelled successfully");
            return ResponseEntity.ok(response);
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Cannot cancel: already used, cancelled, or not found");
        return ResponseEntity.badRequest().body(error);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentBookings(@PathVariable String studentId) {
        var bookings = mealBookingService.getStudentBookings(studentId);
        return ResponseEntity.ok(bookings);
    }
}