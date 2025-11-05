package com.zeromones.repository;

import com.zeromones.model.Employee;
import com.zeromones.model.WorkList;
import com.zeromones.model.WorkListStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkListRepository extends JpaRepository<WorkList, Long> {

    List<WorkList> findByWorkDate(LocalDate workDate);

    List<WorkList> findByEmployee(Employee employee);

    Optional<WorkList> findByEmployeeAndWorkDate(Employee employee, LocalDate workDate);

    List<WorkList> findByMunicipality(String municipality);

    List<WorkList> findByMunicipalityAndWorkDate(String municipality, LocalDate workDate);

    List<WorkList> findByStatus(WorkListStatus status);

    List<WorkList> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT w FROM WorkList w WHERE w.municipality = :municipality AND w.workDate >= :startDate AND w.workDate <= :endDate")
    List<WorkList> findByMunicipalityAndDateRange(
        @Param("municipality") String municipality,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(w) FROM WorkList w WHERE w.municipality = :municipality AND w.status = :status")
    long countByMunicipalityAndStatus(
        @Param("municipality") String municipality,
        @Param("status") WorkListStatus status
    );
}
