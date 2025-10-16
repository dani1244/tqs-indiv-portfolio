package com.example.demo.mealsbooking.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.mealsbooking.entity.MealBooking;

@DataJpaTest
class MealBookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealBookingRepository mealBookingRepository;

    @Test
    void whenFindByToken_thenReturnBooking() {
        // Arrange
        MealBooking booking = new MealBooking("TOKEN123", "student123", "lunch");
        entityManager.persist(booking);
        entityManager.flush();

        // Act
        Optional<MealBooking> found = mealBookingRepository.findByToken("TOKEN123");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("TOKEN123");
        assertThat(found.get().getStudentId()).isEqualTo("student123");
        assertThat(found.get().getServiceShift()).isEqualTo("lunch");
    }

    @Test
    void whenFindByNonExistingToken_thenReturnEmpty() {
        // Act
        Optional<MealBooking> found = mealBookingRepository.findByToken("NONEXISTENT");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindByStudentId_thenReturnAllStudentBookings() {
        // Arrange
        MealBooking booking1 = new MealBooking("TOKEN1", "student123", "lunch");
        MealBooking booking2 = new MealBooking("TOKEN2", "student123", "dinner");
        MealBooking booking3 = new MealBooking("TOKEN3", "student456", "lunch");
        
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        entityManager.flush();

        // Act
        List<MealBooking> studentBookings = mealBookingRepository.findByStudentId("student123");

        // Assert
        assertThat(studentBookings).hasSize(2);
        assertThat(studentBookings)
                .extracting(MealBooking::getToken)
                .containsExactlyInAnyOrder("TOKEN1", "TOKEN2");
    }

    @Test
    void whenFindByServiceShift_thenReturnAllShiftBookings() {
        // Arrange
        MealBooking booking1 = new MealBooking("TOKEN1", "student1", "lunch");
        MealBooking booking2 = new MealBooking("TOKEN2", "student2", "lunch");
        MealBooking booking3 = new MealBooking("TOKEN3", "student3", "dinner");
        
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        entityManager.flush();

        // Act
        List<MealBooking> lunchBookings = mealBookingRepository.findByServiceShift("lunch");
        List<MealBooking> dinnerBookings = mealBookingRepository.findByServiceShift("dinner");

        // Assert
        assertThat(lunchBookings).hasSize(2);
        assertThat(dinnerBookings).hasSize(1);
        
        assertThat(lunchBookings)
                .extracting(MealBooking::getToken)
                .containsExactlyInAnyOrder("TOKEN1", "TOKEN2");
    }


    @Test
    void whenFindActiveBookingsByStudent_thenReturnOnlyNonCancelled() {
    // Arrange
    MealBooking activeBooking1 = new MealBooking("ACTIVE1", "student123", "lunch");
    MealBooking activeBooking2 = new MealBooking("ACTIVE2", "student123", "dinner");
    MealBooking cancelledBooking = new MealBooking("CANCELLED", "student123", "breakfast");
    cancelledBooking.cancel();
    
    entityManager.persist(activeBooking1);
    entityManager.persist(activeBooking2);
    entityManager.persist(cancelledBooking);
    entityManager.flush();

    // Act
    List<MealBooking> activeBookings = mealBookingRepository.findActiveBookingsByStudent("student123");

    // Assert
    assertThat(activeBookings).hasSize(2);
    assertThat(activeBookings)
            .extracting(MealBooking::getToken)
            .containsExactlyInAnyOrder("ACTIVE1", "ACTIVE2");
    assertThat(activeBookings)
            .allMatch(booking -> !booking.isCancelled());
}

    @Test
    void whenSaveBooking_thenAutoGenerateId() {
        // Arrange
        MealBooking booking = new MealBooking("TOKEN123", "student123", "lunch");

        // Act
        MealBooking saved = mealBookingRepository.save(booking);
        entityManager.flush();

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo("TOKEN123");
        assertThat(saved.getStudentId()).isEqualTo("student123");
        assertThat(saved.getServiceShift()).isEqualTo("lunch");
        assertThat(saved.isUsed()).isFalse();
        assertThat(saved.isCancelled()).isFalse();
        assertThat(saved.getReservationTime()).isNotNull();
    }

    @Test
    void whenUpdateBooking_thenPersistChanges() {
        // Arrange
        MealBooking booking = new MealBooking("TOKEN123", "student123", "lunch");
        entityManager.persist(booking);
        entityManager.flush();

        // Act - Update the booking
        booking.markAsUsed();
        MealBooking updated = mealBookingRepository.save(booking);
        entityManager.flush();

        // Assert
        Optional<MealBooking> found = mealBookingRepository.findByToken("TOKEN123");
        assertThat(found).isPresent();
        assertThat(found.get().isUsed()).isTrue();
    }
}