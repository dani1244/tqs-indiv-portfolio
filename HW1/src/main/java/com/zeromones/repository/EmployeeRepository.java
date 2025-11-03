package com.zeromones.repository;

import com.zeromones.model.Employee;
import com.zeromones.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByMunicipality(String municipality);

    List<Employee> findByMunicipalityAndRole(String municipality, EmployeeRole role);

    List<Employee> findByMunicipalityAndActiveTrue(String municipality);

    List<Employee> findByRole(EmployeeRole role);

    List<Employee> findByActiveTrue();

    boolean existsByEmail(String email);
}
