package com.example.demo.mealsbooking.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.mealsbooking.repository.MealBookingRepository;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class MealRestAssuredIT {

    @LocalServerPort
    private int port;

    @Autowired
    private MealBookingRepository mealBookingRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/bookings";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        //LIMPAR DATABASE antes de cada teste
        if (mealBookingRepository != null) {
            mealBookingRepository.deleteAll();
        }
    }

    @Test
    void whenBookMealWithValidData_thenReturnsCreatedBooking() {
        String studentId = "student-restassured-" + System.currentTimeMillis();
        
        given()
            .param("studentId", studentId)
            .param("serviceShift", "lunch")
            
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("studentId", equalTo(studentId))
            .body("serviceShift", equalTo("lunch"))
            .body("token", notNullValue())
            .body("used", equalTo(false))
            .body("cancelled", equalTo(false));
    }

    @Test
    void whenBookMealWithDuplicate_thenReturnsBadRequest() {
        String studentId = "duplicate-restassured-" + System.currentTimeMillis();
        String serviceShift = "dinner";

        // First booking
        given()
            .param("studentId", studentId)
            .param("serviceShift", serviceShift)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Second booking 
        given()
            .param("studentId", studentId)
            .param("serviceShift", serviceShift)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", containsString("already has a reservation"));
    }

    @Test
    void whenGetExistingBooking_thenReturnsBooking() {
        String studentId = "get-restassured-" + System.currentTimeMillis();
        
        // First create a booking and extract token
        String token = given()
            .param("studentId", studentId)
            .param("serviceShift", "lunch")
        .when()
            .post()
        .then()
            .statusCode(201)
            .extract().path("token");

        // Then retrieve it using the extracted token
        given()
        .when()
            .get("/{token}", token)
        .then()
            .statusCode(200)
            .body("token", equalTo(token))
            .body("studentId", equalTo(studentId))
            .body("serviceShift", equalTo("lunch"));
    }

    @Test
    void whenGetNonExistingBooking_thenReturnsNotFound() {
        given()
        .when()
            .get("/{token}", "NONEXISTENT_TOKEN_RESTASSURED")
        .then()
            .statusCode(404)
            .body("error", equalTo("Booking not found"));
    }

    @Test
    void whenCheckInValidBooking_thenReturnsSuccess() {
        String studentId = "checkin-restassured-" + System.currentTimeMillis();
        
        // Create a booking
        String token = given()
            .param("studentId", studentId)
            .param("serviceShift", "lunch")
        .when()
            .post()
        .then()
            .statusCode(201)
            .extract().path("token");

        // Check-in 
        given()
        .when()
            .patch("/{token}/checkin", token)
        .then()
            .statusCode(200)
            .body("message", equalTo("Check-in successful"));

        // Verify it's now used by trying to check-in again 
        given()
        .when()
            .patch("/{token}/checkin", token)
        .then()
            .statusCode(400)
            .body("error", containsString("Cannot check-in"));
    }

    @Test
    void whenCancelValidBooking_thenReturnsSuccess() {
        String studentId = "cancel-restassured-" + System.currentTimeMillis();
        
        // Create a booking
        String token = given()
            .param("studentId", studentId)
            .param("serviceShift", "dinner")
        .when()
            .post()
        .then()
            .statusCode(201)
            .extract().path("token");

        // Cancel 
        given()
        .when()
            .patch("/{token}/cancel", token)
        .then()
            .statusCode(200)
            .body("message", equalTo("Reservation cancelled successfully"));

        // Verify it's cancelled by trying to cancel again 
        given()
        .when()
            .patch("/{token}/cancel", token)
        .then()
            .statusCode(400)
            .body("error", containsString("Cannot cancel"));
    }

    @Test
    void whenGetStudentBookings_thenReturnsList() {
        String studentId = "multi-restassured-" + System.currentTimeMillis();

        // Create multiple bookings for same student
        given()
            .param("studentId", studentId)
            .param("serviceShift", "lunch")
        .when()
            .post()
        .then()
            .statusCode(201);
        
        given()
            .param("studentId", studentId)
            .param("serviceShift", "dinner")
        .when()
            .post()
        .then()
            .statusCode(201);

        // Get all bookings for student
        given()
        .when()
            .get("/student/{studentId}", studentId)
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(2))
            .body("studentId", everyItem(equalTo(studentId)));
    }

    @Test
    void whenBookMealWithMissingStudentId_thenReturnsBadRequest() {
        given()
            .param("serviceShift", "lunch") // Missing studentId
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    void whenBookMealWithMissingServiceShift_thenReturnsBadRequest() {
        given()
            .param("studentId", "test-student") // Missing serviceShift
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    void whenBookMealWithEmptyParameters_thenReturnsBadRequest() {
        given()
            .param("studentId", "")
            .param("serviceShift", "lunch")
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", notNullValue());
    }

    @Test
    void whenCompleteFlow_thenAllOperationsWork() {
        String studentId = "flow-restassured-" + System.currentTimeMillis();
        String serviceShift = "lunch";

        // 1. Create booking
        String token = given()
            .param("studentId", studentId)
            .param("serviceShift", serviceShift)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("studentId", equalTo(studentId))
            .extract().path("token");

        // 2. Verify booking exists
        given()
        .when()
            .get("/{token}", token)
        .then()
            .statusCode(200)
            .body("token", equalTo(token))
            .body("used", equalTo(false))
            .body("cancelled", equalTo(false));

        // 3. Check-in
        given()
        .when()
            .patch("/{token}/checkin", token)
        .then()
            .statusCode(200)
            .body("message", equalTo("Check-in successful"));

        // 4. Verify final state
        given()
        .when()
            .get("/{token}", token)
        .then()
            .statusCode(200)
            .body("used", equalTo(true))
            .body("cancelled", equalTo(false));
    }
}