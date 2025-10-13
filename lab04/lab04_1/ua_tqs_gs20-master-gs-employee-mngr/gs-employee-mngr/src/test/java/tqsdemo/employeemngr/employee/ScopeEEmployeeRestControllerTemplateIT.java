package tqsdemo.employeemngr.employee;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqsdemo.employeemngr.data.Employee;
import tqsdemo.employeemngr.data.EmployeeRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase

// switch AutoConfigureTestDatabase with TestPropertySource to use a real database
// @TestPropertySource( locations = "application-integrationtest.properties")
class ScopeEEmployeeRestControllerTemplateIT {

    // will need to use the server port for the invocation url
    @LocalServerPort
    int randomServerPort;

    // a REST client that is test-friendly
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository.save( new Employee("alice", "alice@deti.com") );
    }

    @AfterEach
    void resetDb() {
        repository.deleteAll();
    }


    @Test
     void whenCreateEmployeeWithSuccess() {
        Employee bob = new Employee("bob", "bob@deti.com");
        restTemplate.postForEntity("/api/employees", bob, Employee.class);

        List<Employee> found = repository.findAll();
        assertThat(found).extracting(Employee::getName).contains("bob");
    }

    @Test
     void givenEmployees_whenGetEmployees_thenStatus200()  {
        createTestEmployee("bob", "bob@deti.com");
        createTestEmployee("alex", "alex@deti.com");


        ResponseEntity<List<Employee>> response = restTemplate
                .exchange("/api/employees", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(Employee::getName).containsExactly("alice", "bob", "alex");

    }


    private void createTestEmployee(String name, String email) {
        Employee emp = new Employee(name, email);
        repository.saveAndFlush(emp);
    }

}
