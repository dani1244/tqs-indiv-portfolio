package com.zeromones.unit;

import com.zeromones.dto.MunicipalityDTO;
import com.zeromones.exception.ExternalApiException;
import com.zeromones.service.MunicipalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MunicipalityService with mocked WebClient
 * Tests isolation from external API
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MunicipalityService Unit Tests with Mocks")
class MunicipalityServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private MunicipalityService municipalityService;

    @BeforeEach
    void setUp() {
        municipalityService = new MunicipalityService(webClient);
        ReflectionTestUtils.setField(municipalityService, "timeout", 5000);
    }

    @Test
    @DisplayName("Should fetch municipalities successfully from external API")
    void testGetAllMunicipalities_Success() {
        // Arrange
        List<MunicipalityDTO> mockMunicipalities = List.of(
            new MunicipalityDTO("Aveiro"),
            new MunicipalityDTO("Porto"),
            new MunicipalityDTO("Lisboa"),
            new MunicipalityDTO("Coimbra")
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.fromIterable(mockMunicipalities));

        // Act
        List<MunicipalityDTO> result = municipalityService.getAllMunicipalities();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Aveiro", result.get(0).getName());
        assertEquals("Porto", result.get(1).getName());
        assertEquals("Lisboa", result.get(2).getName());
        assertEquals("Coimbra", result.get(3).getName());

        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should return empty list when external API returns empty")
    void testGetAllMunicipalities_EmptyResponse() {
        // Arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.empty());

        // Act
        List<MunicipalityDTO> result = municipalityService.getAllMunicipalities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle external API error gracefully")
    void testGetAllMunicipalities_ApiError() {
        // Arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class))
            .thenReturn(Flux.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // Act & Assert
        assertThrows(ExternalApiException.class, () -> municipalityService.getAllMunicipalities());
    }

    @Test
    @DisplayName("Should validate municipality correctly - valid case")
    void testIsValidMunicipality_Valid() {
        // Arrange
        List<MunicipalityDTO> mockMunicipalities = List.of(
            new MunicipalityDTO("Aveiro"),
            new MunicipalityDTO("Porto"),
            new MunicipalityDTO("Lisboa")
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.fromIterable(mockMunicipalities));

        // Act
        boolean result = municipalityService.isValidMunicipality("Aveiro");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate municipality correctly - invalid case")
    void testIsValidMunicipality_Invalid() {
        // Arrange
        List<MunicipalityDTO> mockMunicipalities = List.of(
            new MunicipalityDTO("Aveiro"),
            new MunicipalityDTO("Porto"),
            new MunicipalityDTO("Lisboa")
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.fromIterable(mockMunicipalities));

        // Act
        boolean result = municipalityService.isValidMunicipality("InvalidCity");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate municipality case-insensitively")
    void testIsValidMunicipality_CaseInsensitive() {
        // Arrange
        List<MunicipalityDTO> mockMunicipalities = List.of(
            new MunicipalityDTO("Aveiro"),
            new MunicipalityDTO("Porto")
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.fromIterable(mockMunicipalities));

        // Act
        boolean resultLower = municipalityService.isValidMunicipality("aveiro");

        // Need to mock again for second call
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(MunicipalityDTO.class)).thenReturn(Flux.fromIterable(mockMunicipalities));

        boolean resultUpper = municipalityService.isValidMunicipality("AVEIRO");

        // Assert
        assertTrue(resultLower);
        assertTrue(resultUpper);
    }

    @Test
    @DisplayName("Should return false for null municipality name")
    void testIsValidMunicipality_Null() {
        // Act
        boolean result = municipalityService.isValidMunicipality(null);

        // Assert
        assertFalse(result);
        verify(webClient, never()).get();
    }

    @Test
    @DisplayName("Should return false for empty municipality name")
    void testIsValidMunicipality_Empty() {
        // Act
        boolean result = municipalityService.isValidMunicipality("");

        // Assert
        assertFalse(result);
        verify(webClient, never()).get();
    }

    @Test
    @DisplayName("Should return false for whitespace-only municipality name")
    void testIsValidMunicipality_Whitespace() {
        // Act
        boolean result = municipalityService.isValidMunicipality("   ");

        // Assert
        assertFalse(result);
        verify(webClient, never()).get();
    }
}
