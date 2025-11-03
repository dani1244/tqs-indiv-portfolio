package com.zeromones.dto;

import com.zeromones.model.Employee;
import com.zeromones.model.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "Employee name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "Phone must be 9 digits")
    private String phone;

    @NotNull(message = "Employee role is required")
    private EmployeeRole role;

    @NotBlank(message = "Municipality is required")
    private String municipality;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int assignedRequestsCount;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.phone = employee.getPhone();
        this.role = employee.getRole();
        this.municipality = employee.getMunicipality();
        this.active = employee.isActive();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
        this.assignedRequestsCount = employee.getAssignedRequests() != null ?
                employee.getAssignedRequests().size() : 0;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAssignedRequestsCount() {
        return assignedRequestsCount;
    }

    public void setAssignedRequestsCount(int assignedRequestsCount) {
        this.assignedRequestsCount = assignedRequestsCount;
    }
}
