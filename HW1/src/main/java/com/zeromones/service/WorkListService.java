package com.zeromones.service;

import com.zeromones.dto.WorkListDTO;
import com.zeromones.exception.InvalidBookingException;
import com.zeromones.exception.ResourceNotFoundException;
import com.zeromones.model.Employee;
import com.zeromones.model.ServiceRequest;
import com.zeromones.model.WorkList;
import com.zeromones.repository.EmployeeRepository;
import com.zeromones.repository.ServiceRequestRepository;
import com.zeromones.repository.WorkListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkListService {

    private static final Logger logger = LoggerFactory.getLogger(WorkListService.class);

    private final WorkListRepository workListRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public WorkListService(WorkListRepository workListRepository,
                          EmployeeRepository employeeRepository,
                          ServiceRequestRepository serviceRequestRepository) {
        this.workListRepository = workListRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Transactional
    public WorkListDTO createWorkList(WorkListDTO workListDTO) {
        logger.info("Creating work list for employee ID: {} on date: {}",
                workListDTO.getEmployeeId(), workListDTO.getWorkDate());

        Employee employee = employeeRepository.findById(workListDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + workListDTO.getEmployeeId()));

        if (!employee.isActive()) {
            throw new InvalidBookingException("Cannot assign work to inactive employee");
        }

        // Check if work list already exists for this employee on this date
        if (workListRepository.findByEmployeeAndWorkDate(employee, workListDTO.getWorkDate()).isPresent()) {
            throw new InvalidBookingException("Work list already exists for this employee on this date");
        }

        WorkList workList = new WorkList(workListDTO.getWorkDate(), employee, workListDTO.getMunicipality());
        if (workListDTO.getNotes() != null) {
            workList.setNotes(workListDTO.getNotes());
        }

        WorkList saved = workListRepository.save(workList);
        logger.info("Work list created with ID: {}", saved.getId());
        return new WorkListDTO(saved);
    }

    @Transactional
    public WorkListDTO assignRequestToWorkList(Long workListId, Long requestId) {
        logger.info("Assigning request {} to work list {}", requestId, workListId);

        WorkList workList = workListRepository.findById(workListId)
                .orElseThrow(() -> new ResourceNotFoundException("Work list not found with ID: " + workListId));

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found with ID: " + requestId));

        // Validate municipality matches
        if (!workList.getMunicipality().equalsIgnoreCase(request.getMunicipality())) {
            throw new InvalidBookingException("Request municipality does not match work list municipality");
        }

        // Assign employee to request
        request.setAssignedEmployee(workList.getEmployee());
        serviceRequestRepository.save(request);

        // Add request to work list
        workList.addServiceRequest(request);
        WorkList updated = workListRepository.save(workList);

        logger.info("Request assigned successfully");
        return new WorkListDTO(updated);
    }

    @Transactional(readOnly = true)
    public WorkListDTO getWorkListById(Long id) {
        logger.info("Fetching work list with ID: {}", id);
        WorkList workList = workListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work list not found with ID: " + id));
        return new WorkListDTO(workList);
    }

    @Transactional(readOnly = true)
    public List<WorkListDTO> getWorkListsByDate(LocalDate date) {
        logger.info("Fetching work lists for date: {}", date);
        return workListRepository.findByWorkDate(date).stream()
                .map(WorkListDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkListDTO> getWorkListsByMunicipality(String municipality) {
        logger.info("Fetching work lists for municipality: {}", municipality);
        return workListRepository.findByMunicipality(municipality).stream()
                .map(WorkListDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkListDTO> getWorkListsByEmployee(Long employeeId) {
        logger.info("Fetching work lists for employee ID: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        return workListRepository.findByEmployee(employee).stream()
                .map(WorkListDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkListDTO startWork(Long workListId) {
        logger.info("Starting work for work list ID: {}", workListId);

        WorkList workList = workListRepository.findById(workListId)
                .orElseThrow(() -> new ResourceNotFoundException("Work list not found with ID: " + workListId));

        workList.startWork();
        WorkList updated = workListRepository.save(workList);

        logger.info("Work started successfully");
        return new WorkListDTO(updated);
    }

    @Transactional
    public WorkListDTO completeWork(Long workListId) {
        logger.info("Completing work for work list ID: {}", workListId);

        WorkList workList = workListRepository.findById(workListId)
                .orElseThrow(() -> new ResourceNotFoundException("Work list not found with ID: " + workListId));

        workList.completeWork();
        WorkList updated = workListRepository.save(workList);

        logger.info("Work completed successfully");
        return new WorkListDTO(updated);
    }
}
