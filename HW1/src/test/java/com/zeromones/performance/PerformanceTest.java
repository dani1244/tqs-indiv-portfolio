package com.zeromones.performance;

import com.zeromones.dto.BookingRequestDTO;
import com.zeromones.model.TimeSlot;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic performance tests
 * Tests system behavior under load
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Performance Tests")
class PerformanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Should handle 50 concurrent booking requests")
    void testConcurrentBookings() throws InterruptedException {
        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        // Submit concurrent requests
        for (int i = 0; i < numberOfThreads; i++) {
            final int requestId = i;
            executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    BookingRequestDTO request = createTestBookingWithVariation(requestId);
                    Response response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(request)
                            .post("/api/bookings");

                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    responseTimes.add(responseTime);

                    if (response.getStatusCode() == 201) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all requests to complete (max 30 seconds)
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // Assert results
        assertTrue(completed, "All requests should complete within 30 seconds");
        
        System.out.println("=== Performance Test Results ===");
        System.out.println("Total Requests: " + numberOfThreads);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Failed: " + failureCount.get());
        
        // Calculate response time statistics
        if (!responseTimes.isEmpty()) {
            double avgResponseTime = responseTimes.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            
            long minResponseTime = responseTimes.stream()
                    .mapToLong(Long::longValue)
                    .min()
                    .orElse(0L);
            
            long maxResponseTime = responseTimes.stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L);

            System.out.println("Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
            System.out.println("Min Response Time: " + minResponseTime + " ms");
            System.out.println("Max Response Time: " + maxResponseTime + " ms");
            System.out.println("================================");

            // Performance assertions
            assertTrue(avgResponseTime < 5000, "Average response time should be less than 5 seconds");
            assertTrue(successCount.get() > numberOfThreads * 0.9, 
                    "At least 90% of requests should succeed");
        }
    }

    @Test
    @DisplayName("Should handle rapid successive requests from same client")
    void testRapidSuccessiveRequests() {
        int numberOfRequests = 20;
        List<Long> responseTimes = new ArrayList<>();
        int successCount = 0;

        long totalStartTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfRequests; i++) {
            long startTime = System.currentTimeMillis();
            
            BookingRequestDTO request = createTestBookingWithVariation(i);
            Response response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post("/api/bookings");

            long endTime = System.currentTimeMillis();
            responseTimes.add(endTime - startTime);

            if (response.getStatusCode() == 201) {
                successCount++;
            } else {
                System.out.println("Request " + i + " failed: " + response.getStatusCode() + 
                                 " - " + response.getBody().asString());
            }
        }

        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;

        System.out.println("=== Rapid Successive Requests Test ===");
        System.out.println("Total Requests: " + numberOfRequests);
        System.out.println("Successful: " + successCount);
        System.out.println("Total Time: " + totalTime + " ms");
        System.out.println("Throughput: " + String.format("%.2f", (numberOfRequests * 1000.0 / totalTime)) + " req/sec");
        System.out.println("======================================");

        assertTrue(successCount >= 10, "Should handle at least 10 requests under capacity limits");
        System.out.println("Success rate: " + (successCount * 100.0 / numberOfRequests) + "%");
    }

    @Test
    @DisplayName("Should retrieve bookings efficiently under load")
    void testRetrievalPerformance() throws InterruptedException {
        // First, create some test bookings
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BookingRequestDTO request = createTestBookingWithVariation(i);
            Response response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post("/api/bookings");
            
            if (response.getStatusCode() == 201) {
                tokens.add(response.jsonPath().getString("accessToken"));
            }
        }

        // Now test concurrent retrievals
        int numberOfThreads = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final String token = tokens.get(i % tokens.size());
            executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    Response response = RestAssured.get("/api/bookings/" + token);
                    
                    long endTime = System.currentTimeMillis();
                    responseTimes.add(endTime - startTime);

                    if (response.getStatusCode() == 200) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(20, TimeUnit.SECONDS);
        executorService.shutdown();

        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        System.out.println("=== Retrieval Performance Test ===");
        System.out.println("Total Retrievals: " + numberOfThreads);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("==================================");

        assertTrue(avgResponseTime < 1000, "Retrieval should be fast (< 1 second)");
        assertEquals(numberOfThreads, successCount.get(), "All retrievals should succeed");
    }

    @Test
    @DisplayName("Should handle staff listing with many bookings efficiently")
    void testStaffListingPerformance() {
        // Create multiple bookings
        for (int i = 0; i < 30; i++) {
            BookingRequestDTO request = createTestBookingWithVariation(i);
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post("/api/bookings");
        }

        // Test staff listing performance
        List<Long> responseTimes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            
            Response response = RestAssured.get("/api/staff/bookings");
            
            long endTime = System.currentTimeMillis();
            responseTimes.add(endTime - startTime);

            assertEquals(200, response.getStatusCode());
        }

        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        System.out.println("=== Staff Listing Performance ===");
        System.out.println("Average Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("=================================");

        assertTrue(avgResponseTime < 2000, "Staff listing should be reasonably fast");
    }

    private BookingRequestDTO createTestBookingWithVariation(int id) {
        String[] municipalities = {"Aveiro", "Porto", "Lisboa", "Braga", "Coimbra"};
        String municipality = municipalities[id % municipalities.length];
        
        LocalDate collectionDate = LocalDate.now().plusDays(5 + (id / 5));
        
        BookingRequestDTO request = new BookingRequestDTO();
        request.setMunicipality(municipality);
        request.setItemDescription("Performance test booking " + id + " for " + municipality);
        request.setCollectionDate(collectionDate);
        request.setTimeSlot(TimeSlot.values()[id % TimeSlot.values().length]);
        request.setAddress("Test Address " + id + ", " + municipality);
        request.setContactEmail("perf" + id + "@test.com");
        request.setContactPhone("91234567" + (id % 10));
        request.setNumberOfItems(1 + (id % 3));
        return request;
    }
}