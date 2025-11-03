package com.zeromones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for ZeroMonos Waste Collection System
 * 
 * This system allows citizens to book collection of bulky waste items
 * and staff to manage these requests.
 */
@SpringBootApplication
public class ZeroMonosApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(ZeroMonosApplication.class);

    public static void main(String[] args) {
        logger.info("Starting ZeroMonos Waste Collection System...");
        SpringApplication.run(ZeroMonosApplication.class, args);
        logger.info("Application started successfully!");
    }
}