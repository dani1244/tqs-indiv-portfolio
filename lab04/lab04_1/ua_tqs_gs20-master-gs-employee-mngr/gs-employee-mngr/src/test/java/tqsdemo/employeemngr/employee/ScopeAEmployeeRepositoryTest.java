package tqsdemo.employeemngr.employee;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tqsdemo.employeemngr.data.Employee;
import tqsdemo.employeemngr.data.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataJpaTest limits the test scope to the data access context
 * by default it will rollback transactions after each test
 * tries to autoconfigure a test database
 * note that autoconfiguration will try to use an embedded in-memory database (like H2) if available
 */
@DataJpaTest
class ScopeAEmployeeRepositoryTest {

    // inject a test-friendly Entity Manager
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void whenFindAlexByName_thenReturnAlexEmployee() {
        // arrange a new employee and insert into db; flush force to persist and return the managed entity
        Employee persistedAlex = entityManager.persistFlushFind( new Employee("alex", "alex@deti.com"));

        // test the query method of interest
        Optional<Employee> found = employeeRepository.findByName("alex");
        assertThat(found).isNotEmpty().get().isEqualTo( persistedAlex);
    }


    @Test
    void whenInvalidEmployeeName_thenReturnNull() {
        entityManager.persistFlushFind( new Employee("John", "alex@deti.com"));

        Optional<Employee> fromDb = employeeRepository.findByName("Not John");
        assertThat(fromDb).isEmpty();
    }


    @Test
    void givenSetOfEmployees_whenFindAll_thenReturnAllEmployees() {
        Employee alex = new Employee("alex", "alex@deti.com");
        Employee ron = new Employee("ron", "ron@deti.com");
        Employee bob = new Employee("bob", "bob@deti.com");

        entityManager.persist(alex);
        entityManager.persist(bob);
        entityManager.persist(ron);
        entityManager.flush();

        List<Employee> allEmployees = employeeRepository.findAll();
        assertThat(allEmployees).hasSize(3).extracting(Employee::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());
    }


    @DisplayName("Should find employees whose email ends with a specific domain")
    @Test
    void testFindEmplyeedByOrganizationDomain() {
        // Given
        entityManager.persist(new Employee("alex", "alex@deti.com"));
        entityManager.persist(new Employee("ron", "ron@deti.ua.pt"));
        entityManager.persist(new Employee("bob", "bob@ua.pt"));
        entityManager.flush();

        // When
        List<Employee> results = employeeRepository.findEmployeesByOrganizationDomain("ua.pt");

        // Then
        assertThat(results)
                .hasSize(2)
                .extracting(Employee::getEmail)
                .containsExactlyInAnyOrder(
                        "bob@ua.pt",
                        "ron@deti.ua.pt"
                );
    }

}