package com.zeromones.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeromones.dto.StatusUpdateDTO;
import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import com.zeromones.model.TimeSlot;
import com.zeromones.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StaffController
 * Tests staff management endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("StaffController Integration Tests")
class StaffControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/staff/bookings - Should return all bookings")
    void testGetAllBookings() throws Exception {
        // Create test bookings
        createTestBooking("Aveiro", RequestStatus.RECEIVED);
        createTestBooking("Porto", RequestStatus.ASSIGNED);
        createTestBooking("Lisboa", RequestStatus.COMPLETED);

        mockMvc.perform(get("/api/staff/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].municipality", containsInAnyOrder("Aveiro", "Porto", "Lisboa")));
    }

    @Test
    @DisplayName("GET /api/staff/bookings?municipality=Aveiro - Should filter by municipality")
    void testGetBookingsByMunicipality() throws Exception {
        // Create test bookings
        createTestBooking("Aveiro", RequestStatus.RECEIVED);
        createTestBooking("Aveiro", RequestStatus.ASSIGNED);
        createTestBooking("Porto", RequestStatus.RECEIVED);

        mockMvc.perform(get("/api/staff/bookings")
                .param("municipality", "Aveiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].municipality", everyItem(is("Aveiro"))));
    }

    @Test
    @DisplayName("GET /api/staff/bookings?status=RECEIVED - Should filter by status")
    void testGetBookingsByStatus() throws Exception {
        // Create test bookings
        createTestBooking("Aveiro", RequestStatus.RECEIVED);
        createTestBooking("Porto", RequestStatus.RECEIVED);
        createTestBooking("Lisboa", RequestStatus.COMPLETED);

        mockMvc.perform(get("/api/staff/bookings")
                .param("status", "RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].currentStatus", everyItem(is("RECEIVED"))));
    }

    @Test
    @DisplayName("PUT /api/staff/bookings/{id}/status - Should update status successfully")
    void testUpdateStatus_Success() throws Exception {
        // Create test booking
        ServiceRequest booking = createTestBooking("Aveiro", RequestStatus.RECEIVED);
        Long id = booking.getId();

        // Prepare update
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.ASSIGNED);
        updateDTO.setNotes("Assigned to Team Alpha");

        mockMvc.perform(put("/api/staff/bookings/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.currentStatus").value("ASSIGNED"))
                .andExpect(jsonPath("$.statusHistory").isArray())
                .andExpect(jsonPath("$.statusHistory", hasSize(2)))
                .andExpect(jsonPath("$.statusHistory[1].status").value("ASSIGNED"))
                .andExpect(jsonPath("$.statusHistory[1].notes").value("Assigned to Team Alpha"));
    }

    @Test
    @DisplayName("PUT /api/staff/bookings/{id}/status - Should return 404 for non-existent booking")
    void testUpdateStatus_NotFound() throws Exception {
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.ASSIGNED);

        mockMvc.perform(put("/api/staff/bookings/{id}/status", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/staff/bookings/{id}/status - Should return 400 for missing status")
    void testUpdateStatus_MissingStatus() throws Exception {
        ServiceRequest booking = createTestBooking("Aveiro", RequestStatus.RECEIVED);
        
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setNotes("Test note"); // Missing status

        mockMvc.perform(put("/api/staff/bookings/{id}/status", booking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/staff/bookings/{id}/status - Should return 409 for invalid transition")
    void testUpdateStatus_InvalidTransition() throws Exception {
        // Create booking in RECEIVED status
        ServiceRequest booking = createTestBooking("Aveiro", RequestStatus.RECEIVED);
        
        // Try to transition directly to COMPLETED (not allowed)
        StatusUpdateDTO updateDTO = new StatusUpdateDTO();
        updateDTO.setStatus(RequestStatus.COMPLETED);
        updateDTO.setNotes("Invalid transition test");

        mockMvc.perform(put("/api/staff/bookings/{id}/status", booking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", containsString("Cannot transition")));
    }

    @Test
    @DisplayName("Should handle complete status progression workflow")
    void testCompleteStatusProgressionWorkflow() throws Exception {
        // Create booking
        ServiceRequest booking = createTestBooking("Aveiro", RequestStatus.RECEIVED);
        Long id = booking.getId();

        // Step 1: RECEIVED -> ASSIGNED
        StatusUpdateDTO update1 = new StatusUpdateDTO(RequestStatus.ASSIGNED, "Assigned to team");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("ASSIGNED"));

        // Step 2: ASSIGNED -> IN_PROGRESS
        StatusUpdateDTO update2 = new StatusUpdateDTO(RequestStatus.IN_PROGRESS, "Collection started");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("IN_PROGRESS"));

        // Step 3: IN_PROGRESS -> COMPLETED
        StatusUpdateDTO update3 = new StatusUpdateDTO(RequestStatus.COMPLETED, "Collection completed");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.statusHistory", hasSize(4))); // Initial + 3 updates
    }

    @Test
    @DisplayName("Should handle cancellation at different stages")
    void testCancellationAtDifferentStages() throws Exception {
        // Test cancellation from RECEIVED
        ServiceRequest booking1 = createTestBooking("Aveiro", RequestStatus.RECEIVED);
        StatusUpdateDTO cancel1 = new StatusUpdateDTO(RequestStatus.CANCELLED, "User requested cancellation");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", booking1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancel1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("CANCELLED"));

        // Test cancellation from ASSIGNED
        ServiceRequest booking2 = createTestBooking("Porto", RequestStatus.ASSIGNED);
        StatusUpdateDTO cancel2 = new StatusUpdateDTO(RequestStatus.CANCELLED, "Staff cancellation");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", booking2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancel2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("CANCELLED"));

        // Test cancellation from IN_PROGRESS
        ServiceRequest booking3 = createTestBooking("Lisboa", RequestStatus.IN_PROGRESS);
        StatusUpdateDTO cancel3 = new StatusUpdateDTO(RequestStatus.CANCELLED, "Emergency cancellation");
        mockMvc.perform(put("/api/staff/bookings/{id}/status", booking3.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancel3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("CANCELLED"));
    }

    private ServiceRequest createTestBooking(String municipality, RequestStatus status) {
        ServiceRequest booking = new ServiceRequest();
        booking.setMunicipality(municipality);
        booking.setItemDescription("Test item for " + municipality);
        booking.setCollectionDate(LocalDate.now().plusDays(5));
        booking.setTimeSlot(TimeSlot.MORNING);
        booking.setAddress("Test Address, " + municipality);
        booking.setContactEmail("test@test.com");
        booking.setContactPhone("912345678");
        booking.setNumberOfItems(1);
        booking.setCurrentStatus(status);
        return repository.save(booking);
    }
}