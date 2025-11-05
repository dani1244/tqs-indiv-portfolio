package com.zeromones.service;

import com.zeromones.dto.MunicipalityDTO;
import com.zeromones.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class MunicipalityService {
    
    private static final Logger logger = LoggerFactory.getLogger(MunicipalityService.class);
    
    private final WebClient webClient;
    
    @Value("${external.api.municipalities.timeout:5000}")
    private int timeout;
    
    public MunicipalityService(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public List<MunicipalityDTO> getAllMunicipalities() {
        logger.info("Fetching municipalities from external API");

        try {
            List<MunicipalityDTO> municipalities = webClient.get()
                .retrieve()
                .bodyToFlux(MunicipalityDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .collectList()
                .block();

            logger.info("Successfully fetched {} municipalities", municipalities != null ? municipalities.size() : 0);
            return municipalities != null ? municipalities : new ArrayList<>();

        } catch (Exception e) {
            logger.error("Error fetching municipalities from external API", e);
            throw new ExternalApiException("Failed to fetch municipalities from external API", e);
        }
    }
    
    public boolean isValidMunicipality(String municipalityName) {
        if (municipalityName == null || municipalityName.trim().isEmpty()) {
            return false;
        }
        List<MunicipalityDTO> municipalities = getAllMunicipalities();
        return municipalities.stream()
            .anyMatch(m -> m.getName().equalsIgnoreCase(municipalityName));
    }
}
