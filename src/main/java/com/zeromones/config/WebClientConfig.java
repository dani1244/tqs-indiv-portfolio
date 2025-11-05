package com.zeromones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    
    @Value("${external.api.municipalities.url}")
    private String municipalitiesApiUrl;
    
    @Value("${external.api.municipalities.timeout:5000}")
    private int timeout;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(municipalitiesApiUrl)
            .build();
    }
}
