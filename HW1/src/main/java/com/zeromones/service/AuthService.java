package com.zeromones.service;

import com.zeromones.dto.LoginRequestDTO;
import com.zeromones.dto.LoginResponseDTO;
import com.zeromones.exception.ResourceNotFoundException;
import com.zeromones.model.StaffUser;
import com.zeromones.repository.StaffUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple authentication service
 * Note: In production, use Spring Security with JWT tokens
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // In-memory session storage (for simplicity)
    // In production: use Redis or database
    private final Map<String, StaffUser> activeSessions = new ConcurrentHashMap<>();

    private final StaffUserRepository staffUserRepository;

    public AuthService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        StaffUser user = staffUserRepository.findByUsernameAndPassword(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ).orElseThrow(() -> {
            logger.warn("Failed login attempt for username: {}", loginRequest.getUsername());
            return new ResourceNotFoundException("Invalid username or password");
        });

        if (!user.isActive()) {
            logger.warn("Login attempt for inactive user: {}", loginRequest.getUsername());
            throw new ResourceNotFoundException("User account is inactive");
        }

        // Update last login
        user.updateLastLogin();
        staffUserRepository.save(user);

        // Create session token
        String sessionToken = UUID.randomUUID().toString();
        activeSessions.put(sessionToken, user);

        logger.info("User {} logged in successfully with role {}", user.getUsername(), user.getRole());

        return new LoginResponseDTO(
                sessionToken,
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getMunicipality()
        );
    }

    public boolean validateSession(String sessionToken) {
        return activeSessions.containsKey(sessionToken);
    }

    public StaffUser getUserFromSession(String sessionToken) {
        return activeSessions.get(sessionToken);
    }

    public void logout(String sessionToken) {
        StaffUser user = activeSessions.remove(sessionToken);
        if (user != null) {
            logger.info("User {} logged out", user.getUsername());
        }
    }

    @Transactional
    public void createDefaultUsers() {
        // Create default users if none exist
        if (staffUserRepository.count() == 0) {
            logger.info("Creating default staff users...");

            StaffUser admin = new StaffUser("admin", "admin123", "Administrator", "ADMIN");
            staffUserRepository.save(admin);

            StaffUser manager = new StaffUser("manager", "manager123", "Manager Aveiro", "MANAGER");
            manager.setMunicipality("Aveiro");
            staffUserRepository.save(manager);

            StaffUser operator = new StaffUser("operator", "operator123", "Operator Porto", "OPERATOR");
            operator.setMunicipality("Porto");
            staffUserRepository.save(operator);

            logger.info("Default users created: admin/admin123, manager/manager123, operator/operator123");
        }
    }
}
