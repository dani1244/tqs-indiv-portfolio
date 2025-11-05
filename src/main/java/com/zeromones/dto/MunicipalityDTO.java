package com.zeromones.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for municipality data
 * Simplified to work with string-based APIs
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MunicipalityDTO {
    
    private String name;
    
    // Constructors
    public MunicipalityDTO() {
    }
    
    public MunicipalityDTO(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "MunicipalityDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}