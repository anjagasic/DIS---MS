package se.magnus.microservices.core.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.employee.*;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.employee.persistence.EmployeeRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
class EmployeeServiceApplicationTests {

    @Autowired
    private WebTestClient client;
    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private Sink channels;

    private AbstractMessageChannel input = null;

    @BeforeEach
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();
        repository.deleteAll().block();
    }

    @Test
    public void getEmployeesByGymId() {
        int gymId = 1;

        sendCreateEmployeeEvent(gymId, 1);
        sendCreateEmployeeEvent(gymId, 2);
        sendCreateEmployeeEvent(gymId, 3);

        assertEquals(3, (long) repository.findByGymId(gymId).count().block());
        postAndVerifyEmployee(gymId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].gymId").isEqualTo(gymId)
                .jsonPath("$[2].employeeId").isEqualTo(3);
    }

    @Test
    public void deleteEmployees() {

        int gymId = 1;
        int employeeId = 1;

        sendCreateEmployeeEvent(gymId, employeeId);
        assertEquals(1, (long) repository.findByGymId(gymId).count().block());

        sendDeleteEmployeeEvent(gymId);
        assertEquals(0, (long)repository.findByGymId(gymId).count().block());

        sendDeleteEmployeeEvent(gymId);
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

    private WebTestClient.BodyContentSpec getAndVerifyEmployeesByGymId(String gymIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/employee" + gymIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyEmployee(int gymId, HttpStatus expectedStatus) {
        return getAndVerifyEmployeesByGymId("?gymId=" + gymId, expectedStatus);
    }

    private void sendCreateEmployeeEvent(int gymId, int employeeId) {
        Employee employee = new Employee(gymId, employeeId, "name " + employeeId, "SA");
        Event<Integer, Gym> event = new Event(CREATE, gymId, employee);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteEmployeeEvent(int gymId) {
        Event<Integer, Gym> event = new Event(DELETE, gymId, null);
        input.send(new GenericMessage<>(event));
    }

}
