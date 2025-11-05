package com.zeromones.service;

import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.dto.BookingResponseDTO;
import com.zeromones.dto.StatusUpdateDTO;
import com.zeromones.exception.InvalidBookingException;
import com.zeromones.exception.ResourceNotFoundException;
import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import com.zeromones.repository.ServiceRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    private final ServiceRequestRepository repository;
    private final MunicipalityService municipalityService;
    
    @Value("${booking.max.days.advance:30}")
    private int maxDaysAdvance;
    
    @Value("${booking.min.days.advance:2}")
    private int minDaysAdvance;
    
    @Value("${booking.max.items.per.request:5}")
    private int maxItemsPerRequest;
    
    public BookingService(ServiceRequestRepository repository, MunicipalityService municipalityService) {
        this.repository = repository;
        this.municipalityService = municipalityService;
    }
    
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        logger.info("Creating new booking for municipality: {}", dto.getMunicipality());
        
        validateBookingRequest(dto);
        
        ServiceRequest request = new ServiceRequest();
        request.setMunicipality(dto.getMunicipality());
        request.setItemDescription(dto.getItemDescription());
        request.setCollectionDate(dto.getCollectionDate());
        request.setTimeSlot(dto.getTimeSlot());
        request.setAddress(dto.getAddress());
        request.setContactEmail(dto.getContactEmail());
        request.setContactPhone(dto.getContactPhone());
        request.setNumberOfItems(dto.getNumberOfItems());
        
        ServiceRequest saved = repository.save(request);
        logger.info("Booking created successfully with token: {}", saved.getAccessToken());
        
        return new BookingResponseDTO(saved);
    }
    
    public BookingResponseDTO getBookingByToken(String token) {
        logger.info("Fetching booking with token: {}", token);
        ServiceRequest request = repository.findByAccessToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "token", token));
        return new BookingResponseDTO(request);
    }
    
    public void cancelBooking(String token) {
        logger.info("Cancelling booking with token: {}", token);
        ServiceRequest request = repository.findByAccessToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "token", token));
        
        if (!request.isCancellable()) {
            throw new IllegalStateException("Cannot cancel booking in current status: " + request.getCurrentStatus());
        }
        
        request.updateStatus(RequestStatus.CANCELLED, "Cancelled by user");
        repository.save(request);
        logger.info("Booking cancelled successfully");
    }
    
    public List<BookingResponseDTO> getAllBookings() {
        logger.info("Fetching all bookings");
        return repository.findAll().stream()
            .map(BookingResponseDTO::new)
            .collect(Collectors.toList());
    }
    
    public List<BookingResponseDTO> getBookingsByMunicipality(String municipality) {
        logger.info("Fetching bookings for municipality: {}", municipality);
        return repository.findByMunicipality(municipality).stream()
            .map(BookingResponseDTO::new)
            .collect(Collectors.toList());
    }
    
    public List<BookingResponseDTO> getBookingsByStatus(RequestStatus status) {
        logger.info("Fetching bookings with status: {}", status);
        return repository.findByCurrentStatus(status).stream()
            .map(BookingResponseDTO::new)
            .collect(Collectors.toList());
    }
    
    public BookingResponseDTO updateBookingStatus(Long id, StatusUpdateDTO dto) {
        logger.info("Updating booking {} to status: {}", id, dto.getStatus());
        ServiceRequest request = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        request.updateStatus(dto.getStatus(), dto.getNotes());
        ServiceRequest updated = repository.save(request);
        logger.info("Booking status updated successfully");
        
        return new BookingResponseDTO(updated);
    }
    
    private void validateBookingRequest(BookingRequestDTO dto) {
        // Validate municipality
        if (!municipalityService.isValidMunicipality(dto.getMunicipality())) {
            throw new InvalidBookingException("Invalid municipality: " + dto.getMunicipality());
        }
        
        // Validate date range
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(minDaysAdvance);
        LocalDate maxDate = today.plusDays(maxDaysAdvance);
        
        if (dto.getCollectionDate().isBefore(minDate)) {
            throw new InvalidBookingException(
                String.format("Collection date must be at least %d days in advance", minDaysAdvance)
            );
        }
        
        if (dto.getCollectionDate().isAfter(maxDate)) {
            throw new InvalidBookingException(
                String.format("Collection date cannot be more than %d days in advance", maxDaysAdvance)
            );
        }
        
        // Validate capacity for the date
        long bookingsOnDate = repository.countByCollectionDateAndMunicipality(
            dto.getCollectionDate(), 
            dto.getMunicipality()
        );
        
        if (bookingsOnDate >= 10) { // Max 10 bookings per day per municipality
            throw new InvalidBookingException(
                "Maximum capacity reached for this date and municipality"
            );
        }
        
        logger.info("Booking request validated successfully");
    }
}
