package com.example.demo.mealsbooking.boundary;

import com.example.demo.mealsbooking.entity.MealBooking;
import com.example.demo.mealsbooking.services.MealBookingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealBookingRestController.class)
class MealBookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealBookingServiceImpl mealBookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBookMeal_thenReturnCreatedBooking() throws Exception {
        // Arrange
        MealBooking booking = new MealBooking("TOKEN123", "student123", "lunch");
        booking.setId(1L);
        booking.setReservationTime(LocalDateTime.now());

        when(mealBookingService.bookMeal(any(String.class), any(String.class)))
                .thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/bookings")
                .param("studentId", "student123")
                .param("serviceShift", "lunch")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("TOKEN123"))
                .andExpect(jsonPath("$.studentId").value("student123"))
                .andExpect(jsonPath("$.serviceShift").value("lunch"));
    }

    @Test
    void whenBookMealWithError_thenReturnBadRequest() throws Exception {
        // Arrange
        when(mealBookingService.bookMeal(any(String.class), any(String.class)))
                .thenThrow(new IllegalStateException("No available spots"));

        // Act & Assert
        mockMvc.perform(post("/bookings")
                .param("studentId", "student123")
                .param("serviceShift", "lunch")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No available spots"));
    }

    @Test
    void whenGetBookingByValidToken_thenReturnBooking() throws Exception {
        // Arrange
        MealBooking booking = new MealBooking("TOKEN123", "student123", "lunch");
        booking.setId(1L);

        when(mealBookingService.getReservation("TOKEN123"))
                .thenReturn(Optional.of(booking));

        // Act & Assert
        mockMvc.perform(get("/bookings/TOKEN123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOKEN123"))
                .andExpect(jsonPath("$.studentId").value("student123"));
    }

    @Test
    void whenGetBookingByInvalidToken_thenReturnNotFound() throws Exception {
        // Arrange
        when(mealBookingService.getReservation("INVALID"))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/bookings/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    void whenCheckInValidToken_thenReturnSuccess() throws Exception {
        // Arrange
        when(mealBookingService.checkIn("TOKEN123")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(patch("/bookings/TOKEN123/checkin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Check-in successful"));
    }

    @Test
    void whenCheckInInvalidToken_thenReturnBadRequest() throws Exception {
        // Arrange
        when(mealBookingService.checkIn("INVALID")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(patch("/bookings/INVALID/checkin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot check-in: already used, cancelled, or not found"));
    }

    @Test
    void whenCancelValidBooking_thenReturnSuccess() throws Exception {
        // Arrange
        when(mealBookingService.cancelReservation("TOKEN123")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(patch("/bookings/TOKEN123/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reservation cancelled successfully"));
    }

    @Test
    void whenCancelInvalidBooking_thenReturnBadRequest() throws Exception {
        // Arrange
        when(mealBookingService.cancelReservation("INVALID")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(patch("/bookings/INVALID/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot cancel: already used, cancelled, or not found"));
    }

    @Test
    void whenGetStudentBookings_thenReturnList() throws Exception {
        // Arrange
        MealBooking booking1 = new MealBooking("TOKEN1", "student123", "lunch");
        MealBooking booking2 = new MealBooking("TOKEN2", "student123", "dinner");
        List<MealBooking> bookings = Arrays.asList(booking1, booking2);

        when(mealBookingService.getStudentBookings("student123"))
                .thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings/student/student123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token").value("TOKEN1"))
                .andExpect(jsonPath("$[1].token").value("TOKEN2"));
    }
}