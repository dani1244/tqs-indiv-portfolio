package com.zeromones.dto;

import com.zeromones.model.StaffRole;
import com.zeromones.model.StaffUser;

import java.util.List;
import java.util.stream.Collectors;

public class StaffUserDTO {

    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String municipality;
    private List<String> permissions;

    public StaffUserDTO() {
    }

    public StaffUserDTO(StaffUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.municipality = user.getMunicipality();

        // Get permissions from role
        try {
            StaffRole staffRole = StaffRole.fromString(user.getRole());
            this.permissions = staffRole.getPermissions().stream()
                    .map(perm -> perm.name())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // If role parsing fails, set empty permissions
            this.permissions = new java.util.ArrayList<>();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
