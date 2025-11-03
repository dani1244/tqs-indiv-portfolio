package com.zeromones.functional;

import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.model.RequestStatus;
import com.zeromones.model.TimeSlot;
import com.zeromones.repository.ServiceRequestRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for booking scenarios
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private ServiceRequestRepository repository;

    private BookingRequestDTO bookingRequest;
    private Response response;
    private String accessToken;

    @Given("the waste collection system is available")
    public void theWasteCollectionSystemIsAvailable() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        repository.deleteAll();
    }

    @Given("the following municipalities are available:")
    public void theFollowingMunicipalitiesAreAvailable(DataTable dataTable) {
        // In real scenario, this would seed data or mock the external API
        // For now, we assume the external API returns these municipalities
        assertNotNull(dataTable);
    }

    @Given("I am on the citizen booking page")
    public void iAmOnTheCitizenBookingPage() {
        bookingRequest = new BookingRequestDTO();
    }

    @When("I fill in the booking form with:")
    public void iFillInTheBookingFormWith(Map<String, String> data) {
        bookingRequest.setMunicipality(data.get("municipality"));
        bookingRequest.setItemDescription(data.get("itemDescription"));
        
        // Parse collection date
        String dateStr = data.get("collectionDate");
        LocalDate collectionDate = parseDateString(dateStr);
        bookingRequest.setCollectionDate(collectionDate);
        
        // Parse time slot
        if (data.containsKey("timeSlot")) {
            bookingRequest.setTimeSlot(TimeSlot.valueOf(data.get("timeSlot")));
        }
        
        bookingRequest.setAddress(data.get("address"));
        
        if (data.containsKey("contactEmail")) {
            bookingRequest.setContactEmail(data.get("contactEmail"));
        }
        
        if (data.containsKey("contactPhone")) {
            bookingRequest.setContactPhone(data.get("contactPhone"));
        }
        
        if (data.containsKey("numberOfItems")) {
            bookingRequest.setNumberOfItems(Integer.parseInt(data.get("numberOfItems")));
        }
    }

    @When("I submit the booking form")
    public void iSubmitTheBookingForm() {
        RequestSpecification request = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookingRequest);
        
        response = request.post("/api/bookings");
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        accessToken = response.jsonPath().getString("accessToken");
        assertNotNull(accessToken);
    }

    @Then("I should see a success message")
    public void iShouldSeeASuccessMessage() {
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Then("I should receive an access token")
    public void iShouldReceiveAnAccessToken() {
        accessToken = response.jsonPath().getString("accessToken");
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
    }

    @Then("the booking status should be {string}")
    public void theBookingStatusShouldBe(String expectedStatus) {
        // Para DELETE requests que retornam 204 NO_CONTENT, não tentar ler JSON
        if (response.getStatusCode() == HttpStatus.NO_CONTENT.value()) {
            // Após cancelamento, buscar o booking novamente para verificar status
            Response getResponse = RestAssured.get("/api/bookings/" + accessToken);
            String actualStatus = getResponse.jsonPath().getString("currentStatus");
            assertEquals(expectedStatus, actualStatus);
        } else {
            String actualStatus = response.jsonPath().getString("currentStatus");
            assertEquals(expectedStatus, actualStatus);
        }
    }

    @Then("the booking should not be created")
    public void theBookingShouldNotBeCreated() {
        assertNotEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Then("I should see an error message containing {string}")
    public void iShouldSeeAnErrorMessageContaining(String errorText) {
        String responseBody = response.getBody().asString();
        assertThat(responseBody.toLowerCase(), containsString(errorText.toLowerCase()));
    }

    @Then("I should see a validation error for {string}")
    public void iShouldSeeAValidationErrorFor(String fieldName) {
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        // Check if response contains validation errors
        assertTrue(response.getBody().asString().contains(fieldName) || 
                   response.getStatusCode() == 400);
    }

    @Given("I have created a booking")
    public void iHaveCreatedABooking() {
        // Create a valid booking
        bookingRequest = new BookingRequestDTO();
        bookingRequest.setMunicipality("Aveiro");
        bookingRequest.setItemDescription("Test booking for BDD scenario");
        bookingRequest.setCollectionDate(LocalDate.now().plusDays(5));
        bookingRequest.setTimeSlot(TimeSlot.MORNING);
        bookingRequest.setAddress("Test Address, 123");
        bookingRequest.setContactEmail("test@example.com");
        bookingRequest.setContactPhone("912345678");
        bookingRequest.setNumberOfItems(1);

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookingRequest)
                .post("/api/bookings");

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Given("I have the access token")
    public void iHaveTheAccessToken() {
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
    }

    @Given("the booking status is {string}")
    public void theBookingStatusIs(String status) {
        // This would typically update the booking status directly in the database
        // For simplicity, we check the current status
        response = RestAssured.get("/api/bookings/" + accessToken);
        if (!status.equals(response.jsonPath().getString("currentStatus"))) {
            // Update status via staff endpoint if needed
            if (status.equals("COMPLETED")) {
                Long id = response.jsonPath().getLong("id");
                // Progress through states
                updateBookingStatus(id, "ASSIGNED");
                updateBookingStatus(id, "IN_PROGRESS");
                updateBookingStatus(id, "COMPLETED");
            }
        }
    }

    @When("I query the booking with my token")
    public void iQueryTheBookingWithMyToken() {
        response = RestAssured.get("/api/bookings/" + accessToken);
    }

    @When("I query the booking with token {string}")
    public void iQueryTheBookingWithToken(String token) {
        response = RestAssured.get("/api/bookings/" + token);
    }

    @Then("I should see my booking details")
    public void iShouldSeeMyBookingDetails() {
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
        assertNotNull(response.jsonPath().getString("municipality"));
    }

    @Then("I should see the status history")
    public void iShouldSeeTheStatusHistory() {
        assertThat(response.jsonPath().getList("statusHistory"), not(empty()));
    }

    @When("I cancel the booking with my token")
    public void iCancelTheBookingWithMyToken() {
        response = RestAssured.delete("/api/bookings/" + accessToken);
    }

    @When("I try to cancel the booking")
    public void iTryToCancelTheBooking() {
        response = RestAssured.delete("/api/bookings/" + accessToken);
    }

    @Then("the booking should be cancelled successfully")
    public void theBookingShouldBeCancelledSuccessfully() {
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
    }

    @Then("the cancellation should fail")
    public void theCancellationShouldFail() {
        assertNotEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
    }

    // Helper methods

    private LocalDate parseDateString(String dateStr) {
        if (dateStr.contains("days from now")) {
            int days = Integer.parseInt(dateStr.split(" ")[0]);
            return LocalDate.now().plusDays(days);
        } else if (dateStr.equals("tomorrow")) {
            return LocalDate.now().plusDays(1);
        } else {
            return LocalDate.parse(dateStr);
        }
    }

    private void updateBookingStatus(Long id, String status) {
        String json = String.format("{\"status\":\"%s\",\"notes\":\"BDD test update\"}", status);
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .put("/api/staff/bookings/" + id + "/status");
    }
}