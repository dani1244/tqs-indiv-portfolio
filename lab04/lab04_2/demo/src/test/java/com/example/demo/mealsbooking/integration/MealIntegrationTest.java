package com.example.demo.mealsbooking.integration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.mealsbooking.entity.MealBooking;
import com.example.demo.mealsbooking.repository.MealBookingRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class MealIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MealBookingRepository mealBookingRepository;

    @Test
    void whenBookMeal_thenPersistAndReturnBooking() {
        // Arrange
        String studentId = "integration-student";
        String serviceShift = "lunch";

        // Act
        ResponseEntity<MealBooking> response = restTemplate.postForEntity(
                "/bookings?studentId={studentId}&serviceShift={serviceShift}",
                null, MealBooking.class, studentId, serviceShift);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getStudentId()).isEqualTo(studentId);
        assertThat(response.getBody().getServiceShift()).isEqualTo(serviceShift);

        // Verify persistence in database
        MealBooking persisted = mealBookingRepository.findByToken(response.getBody().getToken()).orElse(null);
        assertThat(persisted).isNotNull();
        assertThat(persisted.getStudentId()).isEqualTo(studentId);
        assertThat(persisted.getServiceShift()).isEqualTo(serviceShift);
    }

    @Test
    void whenGetExistingBooking_thenReturnBooking() {
        // Arrange - Create booking directly in repository
        MealBooking booking = new MealBooking("TESTTOKEN", "test-student", "dinner");
        MealBooking saved = mealBookingRepository.save(booking);

        // Act
        ResponseEntity<MealBooking> response = restTemplate.getForEntity(
                "/bookings/{token}", MealBooking.class, saved.getToken());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(saved.getToken());
        assertThat(response.getBody().getStudentId()).isEqualTo("test-student");
    }

    @Test
    void whenGetNonExistingBooking_thenReturnNotFound() {
        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/bookings/{token}", Map.class, "NONEXISTENT");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Booking not found");
    }

    @Test
    void whenCheckInValidBooking_thenSuccess() {
        // Arrange
        MealBooking booking = new MealBooking("CHECKINTOKEN", "checkin-student", "lunch");
        MealBooking saved = mealBookingRepository.save(booking);

        // Act
        ResponseEntity<Map> response = restTemplate.exchange(
                "/bookings/{token}/checkin",
                HttpMethod.PATCH,
                null,
                Map.class,
                saved.getToken());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Check-in successful");

        // Verify state changed in database
        MealBooking updated = mealBookingRepository.findByToken(saved.getToken()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
    }

    @Test
    void whenCancelValidBooking_thenSuccess() {
        // Arrange
        MealBooking booking = new MealBooking("CANCELTOKEN", "cancel-student", "dinner");
        MealBooking saved = mealBookingRepository.save(booking);

        // Act
        ResponseEntity<Map> response = restTemplate.exchange(
                "/bookings/{token}/cancel",
                HttpMethod.PATCH,
                null,
                Map.class,
                saved.getToken());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Reservation cancelled successfully");

        // Verify state changed in database
        MealBooking updated = mealBookingRepository.findByToken(saved.getToken()).orElseThrow();
        assertThat(updated.isCancelled()).isTrue();
    }

    @Test
    void whenGetStudentBookings_thenReturnList() {
        // Arrange
        MealBooking booking1 = new MealBooking("TOKEN1", "multi-student", "lunch");
        MealBooking booking2 = new MealBooking("TOKEN2", "multi-student", "dinner");
        mealBookingRepository.save(booking1);
        mealBookingRepository.save(booking2);

        // Act
        ResponseEntity<MealBooking[]> response = restTemplate.getForEntity(
                "/bookings/student/{studentId}", MealBooking[].class, "multi-student");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()[0].getStudentId()).isEqualTo("multi-student");
        assertThat(response.getBody()[1].getStudentId()).isEqualTo("multi-student");
    }
}