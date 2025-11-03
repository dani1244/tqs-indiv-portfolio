package com.zeromones.dto;

import com.zeromones.model.WorkList;
import com.zeromones.model.WorkListStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkListDTO {

    private Long id;

    @NotNull(message = "Work date is required")
    private LocalDate workDate;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;

    @NotBlank(message = "Municipality is required")
    private String municipality;

    private WorkListStatus status;
    private List<Long> assignedRequestIds;
    private int totalRequests;
    private int totalItems;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkListDTO() {
        this.assignedRequestIds = new ArrayList<>();
    }

    public WorkListDTO(WorkList workList) {
        this.id = workList.getId();
        this.workDate = workList.getWorkDate();
        this.employeeId = workList.getEmployee() != null ? workList.getEmployee().getId() : null;
        this.employeeName = workList.getEmployee() != null ? workList.getEmployee().getName() : null;
        this.municipality = workList.getMunicipality();
        this.status = workList.getStatus();
        this.assignedRequestIds = workList.getAssignedRequests() != null ?
                workList.getAssignedRequests().stream()
                        .map(req -> req.getId())
                        .collect(Collectors.toList()) : new ArrayList<>();
        this.totalRequests = workList.getTotalRequests();
        this.totalItems = workList.getTotalItems();
        this.notes = workList.getNotes();
        this.createdAt = workList.getCreatedAt();
        this.updatedAt = workList.getUpdatedAt();
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

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public List<Long> getAssignedRequestIds() {
        return assignedRequestIds;
    }

    public void setAssignedRequestIds(List<Long> assignedRequestIds) {
        this.assignedRequestIds = assignedRequestIds;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
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
