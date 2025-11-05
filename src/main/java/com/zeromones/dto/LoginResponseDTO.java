package com.zeromones.dto;

public class LoginResponseDTO {

    private String sessionToken;
    private String username;
    private String fullName;
    private String role;
    private String municipality;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String sessionToken, String username, String fullName, String role, String municipality) {
        this.sessionToken = sessionToken;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.municipality = municipality;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
