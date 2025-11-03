package com.zeromones.controller;

import com.zeromones.dto.EmployeeDTO;
import com.zeromones.model.EmployeeRole;
import com.zeromones.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        logger.info("POST /api/employees - Creating new employee");
        EmployeeDTO created = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        logger.info("GET /api/employees/{} - Fetching employee", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) EmployeeRole role) {

        logger.info("GET /api/employees - Fetching employees (municipality: {}, role: {})", municipality, role);

        List<EmployeeDTO> employees;
        if (municipality != null) {
            employees = employeeService.getEmployeesByMunicipality(municipality);
        } else if (role != null) {
            employees = employeeService.getEmployeesByRole(role);
        } else {
            employees = employeeService.getAllEmployees();
        }

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        logger.info("PUT /api/employees/{} - Updating employee", id);
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long id) {
        logger.info("PATCH /api/employees/{}/deactivate - Deactivating employee", id);
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateEmployee(@PathVariable Long id) {
        logger.info("PATCH /api/employees/{}/activate - Activating employee", id);
        employeeService.activateEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
