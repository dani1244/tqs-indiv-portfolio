package com.example.demo.mealsbooking.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.mealsbooking.entity.MealBooking;
import com.example.demo.mealsbooking.repository.MealBookingRepository;

@ExtendWith(MockitoExtension.class)
class MealBookingServiceTest {

    @Mock
    private MealBookingRepository mealBookingRepository;

    @InjectMocks
    private MealBookingServiceImpl mealBookingService;

    @Test
    void whenBookMealWithValidData_thenReturnBooking() {
        // Arrange
        String studentId = "student123";
        String serviceShift = "lunch";
        
        MealBooking savedBooking = new MealBooking("TOKEN123", studentId, serviceShift);
        savedBooking.setId(1L);

        when(mealBookingRepository.findByStudentId(studentId))
                .thenReturn(Arrays.asList()); // No existing bookings
        when(mealBookingRepository.findByServiceShift(serviceShift))
                .thenReturn(Arrays.asList()); // No bookings for this shift
        when(mealBookingRepository.save(any(MealBooking.class)))
                .thenReturn(savedBooking);

        // Act
        MealBooking result = mealBookingService.bookMeal(studentId, serviceShift);

        // Assert
        assertNotNull(result);
        assertEquals("TOKEN123", result.getToken());
        assertEquals(studentId, result.getStudentId());
        assertEquals(serviceShift, result.getServiceShift());
        verify(mealBookingRepository, times(1)).save(any(MealBooking.class));
    }

    @Test
    void whenBookMealWithDuplicate_thenThrowException() {
        // Arrange
        String studentId = "student123";
        String serviceShift = "lunch";
        
        MealBooking existingBooking = new MealBooking("EXISTING", studentId, serviceShift);
        
        when(mealBookingRepository.findByStudentId(studentId))
                .thenReturn(Arrays.asList(existingBooking));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            mealBookingService.bookMeal(studentId, serviceShift);
        });

        verify(mealBookingRepository, never()).save(any(MealBooking.class));
    }

    @Test
    void whenBookMealWithFullCapacity_thenThrowException() {
        // Arrange
        String studentId = "student123";
        String serviceShift = "lunch";
        
        // Create 100 bookings for the same shift
        MealBooking[] manyBookings = new MealBooking[100];
        for (int i = 0; i < 100; i++) {
            manyBookings[i] = new MealBooking("TOKEN" + i, "student" + i, serviceShift);
        }

        when(mealBookingRepository.findByStudentId(studentId))
                .thenReturn(Arrays.asList()); // No existing bookings for this student
        when(mealBookingRepository.findByServiceShift(serviceShift))
                .thenReturn(Arrays.asList(manyBookings)); // Shift is full

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            mealBookingService.bookMeal(studentId, serviceShift);
        });

        verify(mealBookingRepository, never()).save(any(MealBooking.class));
    }

    @Test
    void whenGetReservationWithValidToken_thenReturnBooking() {
        // Arrange
        String token = "TOKEN123";
        MealBooking booking = new MealBooking(token, "student123", "lunch");
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.of(booking));

        // Act
        Optional<MealBooking> result = mealBookingService.getReservation(token);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(token, result.get().getToken());
        verify(mealBookingRepository, times(1)).findByToken(token);
    }

    @Test
    void whenGetReservationWithInvalidToken_thenReturnEmpty() {
        // Arrange
        String token = "INVALID";
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.empty());

        // Act
        Optional<MealBooking> result = mealBookingService.getReservation(token);

        // Assert
        assertFalse(result.isPresent());
        verify(mealBookingRepository, times(1)).findByToken(token);
    }

    @Test
    void whenCheckInValidBooking_thenReturnTrueAndMarkAsUsed() {
        // Arrange
        String token = "TOKEN123";
        MealBooking booking = new MealBooking(token, "student123", "lunch");
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.of(booking));
        when(mealBookingRepository.save(any(MealBooking.class)))
                .thenReturn(booking);

        // Act
        boolean result = mealBookingService.checkIn(token);

        // Assert
        assertTrue(result);
        assertTrue(booking.isUsed());
        verify(mealBookingRepository, times(1)).save(booking);
    }

    @Test
    void whenCheckInAlreadyUsedBooking_thenReturnFalse() {
        // Arrange
        String token = "TOKEN123";
        MealBooking booking = new MealBooking(token, "student123", "lunch");
        booking.markAsUsed(); // Already used
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.of(booking));

        // Act
        boolean result = mealBookingService.checkIn(token);

        // Assert
        assertFalse(result);
        verify(mealBookingRepository, never()).save(any(MealBooking.class));
    }

    @Test
    void whenCheckInCancelledBooking_thenReturnFalse() {
        // Arrange
        String token = "TOKEN123";
        MealBooking booking = new MealBooking(token, "student123", "lunch");
        booking.cancel(); // Cancelled
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.of(booking));

        // Act
        boolean result = mealBookingService.checkIn(token);

        // Assert
        assertFalse(result);
        verify(mealBookingRepository, never()).save(any(MealBooking.class));
    }

    @Test
    void whenCancelValidBooking_thenReturnTrueAndMarkAsCancelled() {
        // Arrange
        String token = "TOKEN123";
        MealBooking booking = new MealBooking(token, "student123", "lunch");
        
        when(mealBookingRepository.findByToken(token))
                .thenReturn(Optional.of(booking));
        when(mealBookingRepository.save(any(MealBooking.class)))
                .thenReturn(booking);

        // Act
        boolean result = mealBookingService.cancelReservation(token);

        // Assert
        assertTrue(result);
        assertTrue(booking.isCancelled());
        verify(mealBookingRepository, times(1)).save(booking);
    }

    @Test
    void whenGetAvailableSpots_thenReturnCorrectCount() {
        // Arrange
        String shift = "lunch";
        MealBooking booking1 = new MealBooking("TOKEN1", "student1", shift);
        MealBooking booking2 = new MealBooking("TOKEN2", "student2", shift);
        MealBooking cancelledBooking = new MealBooking("TOKEN3", "student3", shift);
        cancelledBooking.cancel();
        
        List<MealBooking> bookings = Arrays.asList(booking1, booking2, cancelledBooking);

        when(mealBookingRepository.findByServiceShift(shift))
                .thenReturn(bookings);

        // Act
        int availableSpots = mealBookingService.getAvailableSpots(shift);

        // Assert
        assertEquals(98, availableSpots); // 100 capacity - 2 active bookings
    }

    @Test
    void whenGetStudentBookings_thenReturnAllBookings() {
        // Arrange
        String studentId = "student123";
        MealBooking booking1 = new MealBooking("TOKEN1", studentId, "lunch");
        MealBooking booking2 = new MealBooking("TOKEN2", studentId, "dinner");
        List<MealBooking> expectedBookings = Arrays.asList(booking1, booking2);

        when(mealBookingRepository.findByStudentId(studentId))
                .thenReturn(expectedBookings);

        // Act
        List<MealBooking> result = mealBookingService.getStudentBookings(studentId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals(studentId, result.get(1).getStudentId());
        verify(mealBookingRepository, times(1)).findByStudentId(studentId);
    }
}