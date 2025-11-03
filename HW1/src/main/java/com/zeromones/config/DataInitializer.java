package com.zeromones.config;

import com.zeromones.model.Employee;
import com.zeromones.model.EmployeeRole;
import com.zeromones.repository.EmployeeRepository;
import com.zeromones.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initializeDefaultUsers(AuthService authService) {
        return args -> {
            authService.createDefaultUsers();
        };
    }

    @Bean
    public CommandLineRunner initializeDefaultEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                logger.info("Initializing default employees...");

                // Employees for Aveiro
                Employee driver1 = new Employee("João Silva", "joao.silva@zeromonos.pt", "912345678",
                    EmployeeRole.DRIVER, "Aveiro");
                employeeRepository.save(driver1);

                Employee collector1 = new Employee("Maria Santos", "maria.santos@zeromonos.pt", "913456789",
                    EmployeeRole.COLLECTOR, "Aveiro");
                employeeRepository.save(collector1);

                // Employees for Porto
                Employee driver2 = new Employee("Pedro Costa", "pedro.costa@zeromonos.pt", "914567890",
                    EmployeeRole.DRIVER, "Porto");
                employeeRepository.save(driver2);

                Employee collector2 = new Employee("Ana Ferreira", "ana.ferreira@zeromonos.pt", "915678901",
                    EmployeeRole.COLLECTOR, "Porto");
                employeeRepository.save(collector2);

                // Employees for Lisboa
                Employee driver3 = new Employee("Carlos Mendes", "carlos.mendes@zeromonos.pt", "916789012",
                    EmployeeRole.DRIVER, "Lisboa");
                employeeRepository.save(driver3);

                Employee collector3 = new Employee("Sofia Oliveira", "sofia.oliveira@zeromonos.pt", "917890123",
                    EmployeeRole.COLLECTOR, "Lisboa");
                employeeRepository.save(collector3);

                // Supervisors
                Employee supervisor1 = new Employee("Ricardo Almeida", "ricardo.almeida@zeromonos.pt", "918901234",
                    EmployeeRole.SUPERVISOR, "Aveiro");
                employeeRepository.save(supervisor1);

                Employee supervisor2 = new Employee("Teresa Rocha", "teresa.rocha@zeromonos.pt", "919012345",
                    EmployeeRole.SUPERVISOR, "Porto");
                employeeRepository.save(supervisor2);

                // Coordinator
                Employee coordinator = new Employee("Bruno Cardoso", "bruno.cardoso@zeromonos.pt", "920123456",
                    EmployeeRole.COORDINATOR, "Lisboa");
                employeeRepository.save(coordinator);

                logger.info("Default employees initialized successfully");
            }
        };
    }
}
