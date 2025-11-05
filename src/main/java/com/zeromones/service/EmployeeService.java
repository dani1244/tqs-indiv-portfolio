package com.zeromones.service;

import com.zeromones.dto.EmployeeDTO;
import com.zeromones.exception.InvalidBookingException;
import com.zeromones.exception.ResourceNotFoundException;
import com.zeromones.model.Employee;
import com.zeromones.model.EmployeeRole;
import com.zeromones.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        logger.info("Creating new employee: {}", employeeDTO.getEmail());

        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new InvalidBookingException("Employee with email " + employeeDTO.getEmail() + " already exists");
        }

        Employee employee = new Employee(
                employeeDTO.getName(),
                employeeDTO.getEmail(),
                employeeDTO.getPhone(),
                employeeDTO.getRole(),
                employeeDTO.getMunicipality()
        );

        Employee saved = employeeRepository.save(employee);
        logger.info("Employee created with ID: {}", saved.getId());
        return new EmployeeDTO(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        logger.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return new EmployeeDTO(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(EmployeeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByMunicipality(String municipality) {
        logger.info("Fetching employees for municipality: {}", municipality);
        return employeeRepository.findByMunicipalityAndActiveTrue(municipality).stream()
                .map(EmployeeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByRole(EmployeeRole role) {
        logger.info("Fetching employees with role: {}", role);
        return employeeRepository.findByRole(role).stream()
                .map(EmployeeDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        logger.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.updateInfo(
                employeeDTO.getName(),
                employeeDTO.getEmail(),
                employeeDTO.getPhone(),
                employeeDTO.getMunicipality()
        );

        if (employeeDTO.getRole() != null) {
            employee.setRole(employeeDTO.getRole());
        }

        Employee updated = employeeRepository.save(employee);
        logger.info("Employee updated successfully");
        return new EmployeeDTO(updated);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        logger.info("Deactivating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.deactivate();
        employeeRepository.save(employee);
        logger.info("Employee deactivated successfully");
    }

    @Transactional
    public void activateEmployee(Long id) {
        logger.info("Activating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.activate();
        employeeRepository.save(employee);
        logger.info("Employee activated successfully");
    }
}
