import refeicoes.MealBookingRequest;
import refeicoes.MealsBookingService;
import refeicoes.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class MealsBookingServiceTest {
    private MealsBookingService bookingService;
    
    @BeforeEach
    void setUp() {
        bookingService = new MealsBookingService();
    }
    
    @Test
    void testSuccessfulBooking() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        
        assertNotNull(reservation);
        assertNotNull(reservation.getToken());
        assertEquals("student123", reservation.getStudentId());
        assertEquals("lunch", reservation.getServiceShift());
        assertFalse(reservation.isUsed());
        assertFalse(reservation.isCancelled());
    }
    
    @Test
    void testDoubleBookingPrevention() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        bookingService.bookMeal(request);
        
        assertThrows(IllegalStateException.class, () -> {
            bookingService.bookMeal(request);
        });
    }
    
    @Test
    void testGetReservation() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        
        Optional<Reservation> found = bookingService.getReservation(reservation.getToken());
        assertTrue(found.isPresent());
        assertEquals(reservation.getToken(), found.get().getToken());
    }
    
    @Test
    void testVerifyValidReservation() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        
        assertTrue(bookingService.verifyReservation(reservation.getToken()));
    }
    
    @Test
    void testVerifyUsedReservation() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        bookingService.checkIn(reservation.getToken());
        
        assertFalse(bookingService.verifyReservation(reservation.getToken()));
    }
    
    @Test
    void testVerifyCancelledReservation() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        bookingService.cancelReservation(reservation.getToken());
        
        assertFalse(bookingService.verifyReservation(reservation.getToken()));
    }
    
    @Test
    void testSuccessfulCheckIn() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        
        assertTrue(bookingService.checkIn(reservation.getToken()));
        assertTrue(reservation.isUsed());
    }
    
    @Test
    void testFailedCheckIn_AlreadyUsed() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        bookingService.checkIn(reservation.getToken());
        
        assertFalse(bookingService.checkIn(reservation.getToken()));
    }
    
    @Test
    void testFailedCheckIn_Cancelled() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        bookingService.cancelReservation(reservation.getToken());
        
        assertFalse(bookingService.checkIn(reservation.getToken()));
    }
    
    @Test
    void testSuccessfulCancellation() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        
        assertTrue(bookingService.cancelReservation(reservation.getToken()));
        assertTrue(reservation.isCancelled());
    }
    
    @Test
    void testFailedCancellation_AlreadyUsed() {
        MealBookingRequest request = new MealBookingRequest("student123", "lunch");
        Reservation reservation = bookingService.bookMeal(request);
        bookingService.checkIn(reservation.getToken());
        
        assertFalse(bookingService.cancelReservation(reservation.getToken()));
    }
    
    @Test
    void testCapacityLimit() {
        bookingService.setShiftCapacity("lunch", 2);
        
        bookingService.bookMeal(new MealBookingRequest("student1", "lunch"));
        bookingService.bookMeal(new MealBookingRequest("student2", "lunch"));
        
        assertThrows(IllegalStateException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest("student3", "lunch"));
        });
    }
    
    @Test
    void testAvailableSpots() {
        bookingService.setShiftCapacity("lunch", 3);
        
        assertEquals(3, bookingService.getAvailableSpots("lunch"));
        
        bookingService.bookMeal(new MealBookingRequest("student1", "lunch"));
        assertEquals(2, bookingService.getAvailableSpots("lunch"));
        
        bookingService.bookMeal(new MealBookingRequest("student2", "lunch"));
        assertEquals(1, bookingService.getAvailableSpots("lunch"));
    }
    
    @Test
    void testAvailableSpotsAfterCheckIn() {
        bookingService.setShiftCapacity("lunch", 2);
        
        Reservation reservation = bookingService.bookMeal(new MealBookingRequest("student1", "lunch"));
        bookingService.bookMeal(new MealBookingRequest("student2", "lunch"));
        
        assertEquals(0, bookingService.getAvailableSpots("lunch"));
        bookingService.checkIn(reservation.getToken());
        assertEquals(1, bookingService.getAvailableSpots("lunch"));
    }
    
    @Test
    void testDifferentShiftsIndependent() {
        bookingService.setShiftCapacity("lunch", 1);
        bookingService.setShiftCapacity("dinner", 1);
        
        bookingService.bookMeal(new MealBookingRequest("student1", "lunch"));
        bookingService.bookMeal(new MealBookingRequest("student1", "dinner")); // Should work
        
        assertThrows(IllegalStateException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest("student2", "lunch"));
        });
    }
    
    @Test
    void testInvalidStudentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest("", "lunch"));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest(null, "lunch"));
        });
    }
    
    @Test
    void testInvalidServiceShift() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest("student123", ""));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookMeal(new MealBookingRequest("student123", null));
        });
    }
    
    @Test
    void testNonExistentReservation() {
        assertFalse(bookingService.verifyReservation("nonexistent"));
        assertFalse(bookingService.checkIn("nonexistent"));
        assertFalse(bookingService.cancelReservation("nonexistent"));
        assertTrue(bookingService.getReservation("nonexistent").isEmpty());
    }
}