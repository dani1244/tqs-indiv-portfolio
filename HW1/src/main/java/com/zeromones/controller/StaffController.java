package com.zeromones.controller;

import com.zeromones.dto.BookingResponseDTO;
import com.zeromones.dto.StatusUpdateDTO;
import com.zeromones.model.RequestStatus;
import com.zeromones.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/bookings")
public class StaffController {
    
    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    
    private final BookingService bookingService;
    
    public StaffController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) RequestStatus status) {
        
        logger.info("GET /api/staff/bookings - municipality: {}, status: {}", municipality, status);
        
        List<BookingResponseDTO> bookings;
        
        if (municipality != null && !municipality.isEmpty()) {
            bookings = bookingService.getBookingsByMunicipality(municipality);
        } else if (status != null) {
            bookings = bookingService.getBookingsByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }
        
        return ResponseEntity.ok(bookings);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO dto) {
        
        logger.info("PUT /api/staff/bookings/{}/status - Updating status to: {}", id, dto.getStatus());
        BookingResponseDTO response = bookingService.updateBookingStatus(id, dto);
        return ResponseEntity.ok(response);
    }
}
