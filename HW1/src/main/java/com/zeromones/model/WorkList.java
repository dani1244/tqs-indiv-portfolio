package com.zeromones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_lists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "work_date"})
})
public class WorkList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Work date is required")
    @Column(nullable = false, name = "work_date")
    private LocalDate workDate;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotBlank(message = "Municipality is required")
    @Column(nullable = false)
    private String municipality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkListStatus status;

    @OneToMany
    @JoinTable(
        name = "work_list_requests",
        joinColumns = @JoinColumn(name = "work_list_id"),
        inverseJoinColumns = @JoinColumn(name = "service_request_id")
    )
    private List<ServiceRequest> assignedRequests = new ArrayList<>();

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public WorkList() {
        this.status = WorkListStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public WorkList(LocalDate workDate, Employee employee, String municipality) {
        this();
        this.workDate = workDate;
        this.employee = employee;
        this.municipality = municipality;
    }

    public void addServiceRequest(ServiceRequest request) {
        if (!this.assignedRequests.contains(request)) {
            this.assignedRequests.add(request);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeServiceRequest(ServiceRequest request) {
        if (this.assignedRequests.remove(request)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void startWork() {
        if (this.status == WorkListStatus.PENDING) {
            this.status = WorkListStatus.IN_PROGRESS;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void completeWork() {
        if (this.status == WorkListStatus.IN_PROGRESS) {
            this.status = WorkListStatus.COMPLETED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void cancelWork(String reason) {
        this.status = WorkListStatus.CANCELLED;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "Cancelled: " + reason;
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalRequests() {
        return assignedRequests.size();
    }

    public int getTotalItems() {
        return assignedRequests.stream()
                .mapToInt(ServiceRequest::getNumberOfItems)
                .sum();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public WorkListStatus getStatus() {
        return status;
    }

    public void setStatus(WorkListStatus status) {
        this.status = status;
    }

    public List<ServiceRequest> getAssignedRequests() {
        return assignedRequests;
    }

    public void setAssignedRequests(List<ServiceRequest> assignedRequests) {
        this.assignedRequests = assignedRequests;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
}
