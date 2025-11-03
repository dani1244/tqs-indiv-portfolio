package com.zeromones.controller;

import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.dto.BookingResponseDTO;
import com.zeromones.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        logger.info("POST /api/bookings - Creating new booking");
        BookingResponseDTO response = bookingService.createBooking(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{token}")
    public ResponseEntity<BookingResponseDTO> getBookingByToken(@PathVariable String token) {
        logger.info("GET /api/bookings/{} - Fetching booking", token);
        BookingResponseDTO response = bookingService.getBookingByToken(token);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{token}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String token) {
        logger.info("DELETE /api/bookings/{} - Cancelling booking", token);
        bookingService.cancelBooking(token);
        return ResponseEntity.noContent().build();
    }
}
