package com.zeromones.performance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes simples de performance sem ferramentas externas
 * Usa apenas JUnit + RestAssured para validar tempos de resposta e throughput
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimplePerformanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    /**
     * Teste 1: Tempo de resposta do endpoint de municípios deve ser < 5000ms
     * (Primeira chamada inclui fetch da API externa, por isso permite até 5 segundos)
     */
    @Test
    void testMunicipalitiesResponseTime() {
        long startTime = System.currentTimeMillis();

        given()
            .when()
                .get("/api/municipalities")
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        System.out.println("Response time: " + responseTime + "ms");
        assertTrue(responseTime < 5000, "Response time should be less than 5000ms (includes external API call)");
    }

    /**
     * Teste 2: Throughput - múltiplas requisições simultâneas
     */
    @Test
    void testConcurrentRequests() throws InterruptedException, ExecutionException {
        int numberOfRequests = 20;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Long>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Enviar 20 requisições simultâneas
        for (int i = 0; i < numberOfRequests; i++) {
            Future<Long> future = executor.submit(() -> {
                long reqStart = System.currentTimeMillis();

                given()
                    .when()
                        .get("/api/municipalities")
                    .then()
                        .statusCode(200);

                return System.currentTimeMillis() - reqStart;
            });
            futures.add(future);
        }

        // Aguardar todas as requisições
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            responseTimes.add(future.get());
        }

        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();

        // Calcular métricas
        double avgResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        long maxResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0);

        double throughput = (numberOfRequests * 1000.0) / totalTime; // req/s

        System.out.println("\n=== Performance Metrics ===");
        System.out.println("Total requests: " + numberOfRequests);
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average response time: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("Max response time: " + maxResponseTime + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " req/s");
        System.out.println("========================\n");

        // Assertions
        assertTrue(avgResponseTime < 1000, "Average response time should be < 1000ms");
        assertTrue(maxResponseTime < 3000, "Max response time should be < 3000ms");
        assertTrue(throughput > 5, "Throughput should be > 5 req/s");
    }

    /**
     * Teste 3: Stress test - criar múltiplos bookings
     */
    @Test
    void testCreateBookingPerformance() throws InterruptedException, ExecutionException {
        int numberOfBookings = 10;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Boolean>> futures = new ArrayList<>();

        String requestBody = """
            {
                "municipality": "Aveiro",
                "itemDescription": "Teste de performance com descrição completa para simular uso real",
                "collectionDate": "2025-11-20",
                "timeSlot": "MORNING",
                "address": "Rua de Teste Performance, 123, 3810-123 Aveiro",
                "contactEmail": "perf@test.com",
                "contactPhone": "912345678",
                "numberOfItems": 1
            }
            """;

        long startTime = System.currentTimeMillis();

        // Criar múltiplos bookings simultaneamente
        for (int i = 0; i < numberOfBookings; i++) {
            Future<Boolean> future = executor.submit(() -> {
                try {
                    given()
                        .contentType("application/json")
                        .body(requestBody)
                    .when()
                        .post("/api/bookings")
                    .then()
                        .statusCode(201)
                        .body("accessToken", notNullValue());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            futures.add(future);
        }

        // Verificar resultados
        int successCount = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                successCount++;
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();

        double successRate = (successCount * 100.0) / numberOfBookings;

        System.out.println("\n=== Booking Creation Performance ===");
        System.out.println("Total bookings attempted: " + numberOfBookings);
        System.out.println("Successful: " + successCount);
        System.out.println("Failed: " + (numberOfBookings - successCount));
        System.out.println("Success rate: " + String.format("%.2f", successRate) + "%");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("===================================\n");

        // Assertions
        assertTrue(successRate >= 90, "Success rate should be at least 90%");
    }

    /**
     * Teste 4: Percentil 95 (P95) - 95% das requisições devem ser rápidas
     */
    @Test
    void testP95ResponseTime() throws InterruptedException, ExecutionException {
        int numberOfRequests = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Long>> futures = new ArrayList<>();

        // Executar 100 requisições
        for (int i = 0; i < numberOfRequests; i++) {
            Future<Long> future = executor.submit(() -> {
                long start = System.currentTimeMillis();
                given()
                    .when()
                        .get("/api/bookings")
                    .then()
                        .statusCode(200);
                return System.currentTimeMillis() - start;
            });
            futures.add(future);
        }

        // Coletar tempos de resposta
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            responseTimes.add(future.get());
        }
        executor.shutdown();

        // Ordenar e calcular P95
        responseTimes.sort(Long::compareTo);
        int p95Index = (int) (numberOfRequests * 0.95) - 1;
        long p95 = responseTimes.get(p95Index);

        long p50 = responseTimes.get(numberOfRequests / 2); // Mediana
        long p99 = responseTimes.get((int) (numberOfRequests * 0.99) - 1);

        System.out.println("\n=== Response Time Percentiles ===");
        System.out.println("P50 (median): " + p50 + "ms");
        System.out.println("P95: " + p95 + "ms");
        System.out.println("P99: " + p99 + "ms");
        System.out.println("===============================\n");

        // Assertions
        assertTrue(p50 < 500, "P50 should be < 500ms");
        assertTrue(p95 < 1500, "P95 should be < 1500ms");
        assertTrue(p99 < 3000, "P99 should be < 3000ms");
    }
}
