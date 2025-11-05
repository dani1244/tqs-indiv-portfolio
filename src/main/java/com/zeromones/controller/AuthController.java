package com.zeromones.controller;

import com.zeromones.dto.LoginRequestDTO;
import com.zeromones.dto.LoginResponseDTO;
import com.zeromones.dto.StaffUserDTO;
import com.zeromones.model.StaffUser;
import com.zeromones.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("POST /api/auth/login - Login request for user: {}", loginRequest.getUsername());
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {
        logger.info("POST /api/auth/logout");
        if (sessionToken != null) {
            authService.logout(sessionToken);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateSession(@RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {
        logger.info("GET /api/auth/validate");
        boolean valid = sessionToken != null && authService.validateSession(sessionToken);
        return ResponseEntity.ok(valid);
    }

    @GetMapping("/me")
    public ResponseEntity<StaffUserDTO> getCurrentUser(@RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {
        logger.info("GET /api/auth/me");

        if (sessionToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No session token provided");
        }

        StaffUser user = authService.getUserFromSession(sessionToken);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired session");
        }

        return ResponseEntity.ok(new StaffUserDTO(user));
    }
}
