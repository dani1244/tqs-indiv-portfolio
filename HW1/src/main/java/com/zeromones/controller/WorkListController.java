package com.zeromones.controller;

import com.zeromones.dto.WorkListDTO;
import com.zeromones.service.WorkListService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worklists")
public class WorkListController {

    private static final Logger logger = LoggerFactory.getLogger(WorkListController.class);
    private final WorkListService workListService;

    public WorkListController(WorkListService workListService) {
        this.workListService = workListService;
    }

    @PostMapping
    public ResponseEntity<WorkListDTO> createWorkList(@Valid @RequestBody WorkListDTO workListDTO) {
        logger.info("POST /api/worklists - Creating new work list");
        WorkListDTO created = workListService.createWorkList(workListDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{workListId}/requests/{requestId}")
    public ResponseEntity<WorkListDTO> assignRequest(
            @PathVariable Long workListId,
            @PathVariable Long requestId) {

        logger.info("POST /api/worklists/{}/requests/{} - Assigning request to work list", workListId, requestId);
        WorkListDTO updated = workListService.assignRequestToWorkList(workListId, requestId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkListDTO> getWorkList(@PathVariable Long id) {
        logger.info("GET /api/worklists/{} - Fetching work list", id);
        WorkListDTO workList = workListService.getWorkListById(id);
        return ResponseEntity.ok(workList);
    }

    @GetMapping
    public ResponseEntity<List<WorkListDTO>> getWorkLists(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) Long employeeId) {

        logger.info("GET /api/worklists - Fetching work lists (date: {}, municipality: {}, employeeId: {})",
                date, municipality, employeeId);

        List<WorkListDTO> workLists;
        if (date != null) {
            workLists = workListService.getWorkListsByDate(date);
        } else if (municipality != null) {
            workLists = workListService.getWorkListsByMunicipality(municipality);
        } else if (employeeId != null) {
            workLists = workListService.getWorkListsByEmployee(employeeId);
        } else {
            // Return work lists for today by default
            workLists = workListService.getWorkListsByDate(LocalDate.now());
        }

        return ResponseEntity.ok(workLists);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<WorkListDTO> startWork(@PathVariable Long id) {
        logger.info("PATCH /api/worklists/{}/start - Starting work", id);
        WorkListDTO updated = workListService.startWork(id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<WorkListDTO> completeWork(@PathVariable Long id) {
        logger.info("PATCH /api/worklists/{}/complete - Completing work", id);
        WorkListDTO updated = workListService.completeWork(id);
        return ResponseEntity.ok(updated);
    }
}
