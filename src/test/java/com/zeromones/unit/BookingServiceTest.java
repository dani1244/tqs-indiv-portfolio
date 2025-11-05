package com.zeromones.unit;

import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.dto.BookingResponseDTO;
import com.zeromones.dto.StatusUpdateDTO;
import com.zeromones.exception.InvalidBookingException;
import com.zeromones.exception.ResourceNotFoundException;
import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import com.zeromones.model.TimeSlot;
import com.zeromones.repository.ServiceRequestRepository;
import com.zeromones.service.BookingService;
import com.zeromones.service.MunicipalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService
 * Tests business logic in isolation using mocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingServiceTest {

    @Mock
    private ServiceRequestRepository repository;

    @Mock
    private MunicipalityService municipalityService;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO validRequest;
    private ServiceRequest savedRequest;

    @BeforeEach
    void setUp() {
        // Set configuration values
        ReflectionTestUtils.setField(bookingService, "maxDaysAdvance", 30);
        ReflectionTestUtils.setField(bookingService, "minDaysAdvance", 2);
        ReflectionTestUtils.setField(bookingService, "maxItemsPerRequest", 5);

        // Setup valid request DTO
        validRequest = new BookingRequestDTO();
        validRequest.setMunicipality("Aveiro");
        validRequest.setItemDescription("Old sofa and two chairs from living room");
        validRequest.setCollectionDate(LocalDate.now().plusDays(5));
        validRequest.setTimeSlot(TimeSlot.MORNING);
        validRequest.setAddress("Rua de Aveiro, 123, 3810-123 Aveiro");
        validRequest.setContactEmail("test@example.com");
        validRequest.setContactPhone("912345678");
        validRequest.setNumberOfItems(2);

        // Setup saved request entity
        savedRequest = new ServiceRequest();
        savedRequest.setId(1L);
        savedRequest.setMunicipality("Aveiro");
        savedRequest.setItemDescription("Old sofa and two chairs from living room");
        savedRequest.setCollectionDate(LocalDate.now().plusDays(5));
        savedRequest.setTimeSlot(TimeSlot.MORNING);
        savedRequest.setAddress("Rua de Aveiro, 123, 3810-123 Aveiro");
        savedRequest.setContactEmail("test@example.com");
        savedRequest.setContactPhone("912345678");
        savedRequest.setNumberOfItems(2);
    }

    @Test
    @DisplayName("Should create booking successfully with valid data")
    void testCreateBooking_Success() {
        // Arrange
        when(municipalityService.isValidMunicipality("Aveiro")).thenReturn(true);
        when(repository.countByCollectionDateAndMunicipality(any(), eq("Aveiro"))).thenReturn(5L);
        when(repository.save(any(ServiceRequest.class))).thenReturn(savedRequest);

        // Act
        BookingResponseDTO response = bookingService.createBooking(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Aveiro", response.getMunicipality());
        assertEquals(RequestStatus.RECEIVED, response.getCurrentStatus());
        assertNotNull(response.getAccessToken());
        assertEquals(1, response.getStatusHistory().size());
        
        verify(municipalityService, times(1)).isValidMunicipality("Aveiro");
        verify(repository, times(1)).countByCollectionDateAndMunicipality(any(), eq("Aveiro"));
        verify(repository, times(1)).save(any(ServiceRequest.class));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException for invalid municipality")
    void testCreateBooking_InvalidMunicipality() {
        // Arrange
        validRequest.setMunicipality("InvalidCity");
        when(municipalityService.isValidMunicipality("InvalidCity")).thenReturn(false);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
            InvalidBookingException.class,
            () -> bookingService.createBooking(validRequest)
        );
        
        assertTrue(exception.getMessage().contains("Invalid municipality"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException for date too soon")
    void testCreateBooking_DateTooSoon() {
        // Arrange
        validRequest.setCollectionDate(LocalDate.now().plusDays(1)); // Only 1 day advance
        when(municipalityService.isValidMunicipality("Aveiro")).thenReturn(true);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
            InvalidBookingException.class,
            () -> bookingService.createBooking(validRequest)
        );
        
        assertTrue(exception.getMessage().contains("at least 2 days in advance"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException for date too far")
    void testCreateBooking_DateTooFar() {
        // Arrange
        validRequest.setCollectionDate(LocalDate.now().plusDays(35)); // More than 30 days
        when(municipalityService.isValidMunicipality("Aveiro")).thenReturn(true);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
            InvalidBookingException.class,
            () -> bookingService.createBooking(validRequest)
        );
        
        assertTrue(exception.getMessage().contains("more than 30 days"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when capacity exceeded")
    void testCreateBooking_CapacityExceeded() {
        // Arrange
        when(municipalityService.isValidMunicipality("Aveiro")).thenReturn(true);
        when(repository.countByCollectionDateAndMunicipality(any(), eq("Aveiro"))).thenReturn(10L);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
            InvalidBookingException.class,
            () -> bookingService.createBooking(validRequest)
        );
        
        assertTrue(exception.getMessage().contains("Maximum capacity reached"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get booking by token successfully")
    void testGetBookingByToken_Success() {
        // Arrange
        String token = "test-token-123";
        when(repository.findByAccessToken(token)).thenReturn(Optional.of(savedRequest));

        // Act
        BookingResponseDTO response = bookingService.getBookingByToken(token);

        // Assert
        assertNotNull(response);
        assertEquals("Aveiro", response.getMunicipality());
        verify(repository, times(1)).findByAccessToken(token);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid token")
    void testGetBookingByToken_NotFound() {
        // Arrange
        String token = "invalid-token";
        when(repository.findByAccessToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> bookingService.getBookingByToken(token)
        );
    }

    @Test
    @DisplayName("Should cancel booking successfully when status is RECEIVED")
    void testCancelBooking_Success() {
        // Arrange
        String token = "test-token-123";
        savedRequest.setCurrentStatus(RequestStatus.RECEIVED);
        when(repository.findByAccessToken(token)).thenReturn(Optional.of(savedRequest));
        when(repository.save(any(ServiceRequest.class))).thenReturn(savedRequest);

        // Act
        assertDoesNotThrow(() -> bookingService.cancelBooking(token));

        // Assert
        verify(repository, times(1)).findByAccessToken(token);
        verify(repository, times(1)).save(any(ServiceRequest.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling completed booking")
    void testCancelBooking_CompletedStatus() {
        // Arrange
        String token = "test-token-123";
        savedRequest.setCurrentStatus(RequestStatus.COMPLETED);
        when(repository.findByAccessToken(token)).thenReturn(Optional.of(savedRequest));

        // Act & Assert
        assertThrows(
            IllegalStateException.class,
            () -> bookingService.cancelBooking(token)
        );
        
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all bookings")
    void testGetAllBookings() {
        // Arrange
        List<ServiceRequest> requests = Arrays.asList(savedRequest, savedRequest);
        when(repository.findAll()).thenReturn(requests);

        // Act
        List<BookingResponseDTO> responses = bookingService.getAllBookings();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get bookings by municipality")
    void testGetBookingsByMunicipality() {
        // Arrange
        List<ServiceRequest> requests = Arrays.asList(savedRequest);
        when(repository.findByMunicipality("Aveiro")).thenReturn(requests);

        // Act
        List<BookingResponseDTO> responses = bookingService.getBookingsByMunicipality("Aveiro");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Aveiro", responses.get(0).getMunicipality());
        verify(repository, times(1)).findByMunicipality("Aveiro");
    }

    @Test
    @DisplayName("Should get bookings by status")
    void testGetBookingsByStatus() {
        // Arrange
        List<ServiceRequest> requests = Arrays.asList(savedRequest);
        when(repository.findByCurrentStatus(RequestStatus.RECEIVED)).thenReturn(requests);

        // Act
        List<BookingResponseDTO> responses = bookingService.getBookingsByStatus(RequestStatus.RECEIVED);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(RequestStatus.RECEIVED, responses.get(0).getCurrentStatus());
        verify(repository, times(1)).findByCurrentStatus(RequestStatus.RECEIVED);
    }

    @Test
    @DisplayName("Should update booking status successfully")
    void testUpdateBookingStatus_Success() {
        // Arrange
        Long id = 1L;
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.ASSIGNED);
        updateDTO.setNotes("Assigned to team Alpha");
        
        savedRequest.setCurrentStatus(RequestStatus.RECEIVED);
        when(repository.findById(id)).thenReturn(Optional.of(savedRequest));
        when(repository.save(any(ServiceRequest.class))).thenReturn(savedRequest);

        // Act
        BookingResponseDTO response = bookingService.updateBookingStatus(id, updateDTO);

        // Assert
        assertNotNull(response);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(ServiceRequest.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent booking")
    void testUpdateBookingStatus_NotFound() {
        // Arrange
        Long id = 999L;
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.ASSIGNED);
        
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> bookingService.updateBookingStatus(id, updateDTO)
        );
        
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException for invalid status transition")
    void testUpdateBookingStatus_InvalidTransition() {
        // Arrange
        Long id = 1L;
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.COMPLETED); // Can't go directly to COMPLETED from RECEIVED
        
        savedRequest.setCurrentStatus(RequestStatus.RECEIVED);
        when(repository.findById(id)).thenReturn(Optional.of(savedRequest));

        // Act & Assert
        assertThrows(
            IllegalStateException.class,
            () -> bookingService.updateBookingStatus(id, updateDTO)
        );
        
        verify(repository, never()).save(any());
    }
}