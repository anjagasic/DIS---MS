package se.magnus.microservices.core.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.employee.*;
import se.magnus.microservices.core.employee.persistence.EmployeeRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
class EmployeeServiceApplicationTests {

    @Autowired
    private WebTestClient client;
    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void getEmployeesByGymId() {

        int gymId = 1;

        postAndVerifyEmployee(gymId, 1, OK);
        postAndVerifyEmployee(gymId, 2, OK);
        postAndVerifyEmployee(gymId, 3, OK);

        assertEquals(3, repository.findByGymId(gymId).size());
    }

    @Test
    public void deleteEmployees() {

        int gymId = 1;
        int employeeId = 1;

        postAndVerifyEmployee(gymId, employeeId, OK);
        assertEquals(1, repository.findByGymId(gymId).size());

        deleteAndVerifyEmployeesByInsuranceCompanyId(gymId, OK);
        assertEquals(0, repository.findByGymId(gymId).size());

        deleteAndVerifyEmployeesByInsuranceCompanyId(gymId, OK);
    }

    @Test
    public void getEmployeesMissingParameter() {

        getAndVerifyEmployeesByGymId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/employee")
                .jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
    }

    @Test
    public void getEmployeesInvalidParameter() {

        getAndVerifyEmployeesByGymId("?gymId=no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/employee")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getEmployeesNotFound() {

        getAndVerifyEmployeesByGymId("?gymId=113", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getEmployeesInvalidParameterNegativeValue() {

        int gymIdInvalid = -1;

        getAndVerifyEmployeesByGymId("?gymId=" + gymIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/employee")
                .jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
    }

    @Test
    public void postEmployee() {
        int gymId = 1;
        int employeeId = 1;
        postAndVerifyEmployee(gymId, employeeId, OK);
        assertNotNull(repository.findByEmployeeId(employeeId));
    }

    private WebTestClient.BodyContentSpec getAndVerifyEmployeesByGymId(String gymIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/employee" + gymIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyEmployee(int gymId, int employeeId, HttpStatus expectedStatus) {
        Employee employee = new Employee(gymId, employeeId, "name 1", "SA");
        return client.post()
                .uri("/employee")
                .body(just(employee), Employee.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyEmployeesByInsuranceCompanyId(int gymId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("/employee?gymId=" + gymId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus).
                expectBody();
    }
}
