package com.zeromones.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import com.zeromones.model.TimeSlot;
import com.zeromones.repository.ServiceRequestRepository;
import com.zeromones.service.MunicipalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BookingController
 * Tests the full REST API with real database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("BookingController Integration Tests")
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceRequestRepository repository;

    @MockBean
    private MunicipalityService municipalityService;

    private BookingRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Mock municipality validation to always return true for common municipalities
        when(municipalityService.isValidMunicipality(anyString())).thenReturn(true);

        validRequest = new BookingRequestDTO();
        validRequest.setMunicipality("Aveiro");
        validRequest.setItemDescription("Old sofa and two chairs from living room");
        validRequest.setCollectionDate(LocalDate.now().plusDays(5));
        validRequest.setTimeSlot(TimeSlot.MORNING);
        validRequest.setAddress("Rua de Aveiro, 123, 3810-123 Aveiro");
        validRequest.setContactEmail("test@example.com");
        validRequest.setContactPhone("912345678");
        validRequest.setNumberOfItems(2);
    }

    @Test
    @DisplayName("POST /api/bookings - Should create booking successfully")
    void testCreateBooking_Success() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.municipality").value("Aveiro"))
                .andExpect(jsonPath("$.itemDescription").value(containsString("sofa")))
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"))
                .andExpect(jsonPath("$.numberOfItems").value(2))
                .andExpect(jsonPath("$.statusHistory").isArray())
                .andExpect(jsonPath("$.statusHistory[0].status").value("RECEIVED"))
                .andExpect(jsonPath("$.statusHistory[0].notes").value("Request created"));
    }

    @Test
    @DisplayName("POST /api/bookings - Should return 400 for missing municipality")
    void testCreateBooking_MissingMunicipality() throws Exception {
        validRequest.setMunicipality(null);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.municipality").exists());
    }

    @Test
    @DisplayName("POST /api/bookings - Should return 400 for short description")
    void testCreateBooking_ShortDescription() throws Exception {
        validRequest.setItemDescription("Short");

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.itemDescription").exists());
    }

    @Test
    @DisplayName("POST /api/bookings - Should return 400 for past date")
    void testCreateBooking_PastDate() throws Exception {
        validRequest.setCollectionDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bookings - Should return 400 for invalid email")
    void testCreateBooking_InvalidEmail() throws Exception {
        validRequest.setContactEmail("invalid-email");

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.contactEmail").exists());
    }

    @Test
    @DisplayName("POST /api/bookings - Should return 400 for invalid phone")
    void testCreateBooking_InvalidPhone() throws Exception {
        validRequest.setContactPhone("12345"); // Less than 9 digits

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.contactPhone").exists());
    }

    @Test
    @DisplayName("GET /api/bookings/{token} - Should return booking by token")
    void testGetBookingByToken_Success() throws Exception {
        // Create a booking first
        ServiceRequest booking = createTestBooking();
        String token = booking.getAccessToken();

        mockMvc.perform(get("/api/bookings/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.municipality").value("Aveiro"))
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"));
    }

    @Test
    @DisplayName("GET /api/bookings/{token} - Should return 404 for invalid token")
    void testGetBookingByToken_NotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/{token}", "invalid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("DELETE /api/bookings/{token} - Should cancel booking")
    void testCancelBooking_Success() throws Exception {
        // Create a booking first
        ServiceRequest booking = createTestBooking();
        String token = booking.getAccessToken();

        mockMvc.perform(delete("/api/bookings/{token}", token))
                .andExpect(status().isNoContent());

        // Verify it was cancelled
        mockMvc.perform(get("/api/bookings/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("CANCELLED"));
    }

    @Test
    @DisplayName("DELETE /api/bookings/{token} - Should return 404 for invalid token")
    void testCancelBooking_NotFound() throws Exception {
        mockMvc.perform(delete("/api/bookings/{token}", "invalid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/bookings/{token} - Should return 409 for completed booking")
    void testCancelBooking_AlreadyCompleted() throws Exception {
        // Create and complete a booking
        ServiceRequest booking = createTestBooking();
        booking.setCurrentStatus(RequestStatus.COMPLETED);
        repository.save(booking);
        String token = booking.getAccessToken();

        mockMvc.perform(delete("/api/bookings/{token}", token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should handle full booking lifecycle")
    void testFullBookingLifecycle() throws Exception {
        // 1. Create booking
        String createResponse = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(createResponse).get("accessToken").asText();

        // 2. Get booking
        mockMvc.perform(get("/api/bookings/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"));

        // 3. Cancel booking
        mockMvc.perform(delete("/api/bookings/{token}", token))
                .andExpect(status().isNoContent());

        // 4. Verify cancelled
        mockMvc.perform(get("/api/bookings/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("CANCELLED"))
                .andExpect(jsonPath("$.statusHistory").isArray())
                .andExpect(jsonPath("$.statusHistory", hasSize(2)));
    }

    private ServiceRequest createTestBooking() {
        ServiceRequest booking = new ServiceRequest();
        booking.setMunicipality("Aveiro");
        booking.setItemDescription("Test item description for integration test");
        booking.setCollectionDate(LocalDate.now().plusDays(5));
        booking.setTimeSlot(TimeSlot.MORNING);
        booking.setAddress("Test Address, 123");
        booking.setContactEmail("test@test.com");
        booking.setContactPhone("912345678");
        booking.setNumberOfItems(1);
        return repository.save(booking);
    }
}