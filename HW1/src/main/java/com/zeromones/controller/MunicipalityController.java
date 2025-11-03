package com.zeromones.controller;

import com.zeromones.dto.MunicipalityDTO;
import com.zeromones.service.MunicipalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/municipalities")
public class MunicipalityController {
    
    private static final Logger logger = LoggerFactory.getLogger(MunicipalityController.class);
    
    private final MunicipalityService municipalityService;
    
    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }
    
    @GetMapping
    public ResponseEntity<List<MunicipalityDTO>> getAllMunicipalities() {
        logger.info("GET /api/municipalities - Fetching all municipalities");
        List<MunicipalityDTO> municipalities = municipalityService.getAllMunicipalities();
        return ResponseEntity.ok(municipalities);
    }
}
