package com.zeromones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{9}$", message = "Phone must be 9 digits")
    @Column(length = 15)
    private String phone;

    @NotNull(message = "Employee role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @NotBlank(message = "Municipality is required")
    @Column(nullable = false)
    private String municipality;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignedEmployee", cascade = CascadeType.ALL)
    private List<ServiceRequest> assignedRequests = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<WorkList> workLists = new ArrayList<>();

    public Employee() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Employee(String name, String email, String phone, EmployeeRole role, String municipality) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.municipality = municipality;
    }

    public void updateInfo(String name, String email, String phone, String municipality) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
        if (municipality != null) this.municipality = municipality;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
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

    public List<ServiceRequest> getAssignedRequests() {
        return assignedRequests;
    }

    public void setAssignedRequests(List<ServiceRequest> assignedRequests) {
        this.assignedRequests = assignedRequests;
    }

    public List<WorkList> getWorkLists() {
        return workLists;
    }

    public void setWorkLists(List<WorkList> workLists) {
        this.workLists = workLists;
    }
}
