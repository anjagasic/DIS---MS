package se.magnus.microservices.composite.gym;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.program.Program;
import se.magnus.microservices.composite.gym.services.GymCompositeIntegration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class GymCompositeServiceApplicationTests {

	private static final int GYM_ID_OK = 1;
	private static final int GYM_ID_NOT_FOUND = 113;
	private static final int GYM_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private GymCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getGym(GYM_ID_OK)).
				thenReturn(Mono.just(new Gym(GYM_ID_OK, "Test Name", "Test address", "mock-address")));
		when(compositeIntegration.getClients(GYM_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Client(GYM_ID_OK, 1, "Test full name", "Test gender", "Test age", "mock address"))));
		when(compositeIntegration.getEmployees(GYM_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Employee(GYM_ID_OK, 1, "Test full name", "mock address"))));
		when(compositeIntegration.getPrograms(GYM_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Program(GYM_ID_OK, 1, "Test name", "mock address"))));

		when(compositeIntegration.getGym(GYM_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + GYM_ID_NOT_FOUND));

		when(compositeIntegration.getGym(GYM_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + GYM_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getGymById() {
		getAndVerifyGym(GYM_ID_OK, OK)
				.jsonPath("$.gymId").isEqualTo(GYM_ID_OK)
				.jsonPath("$.clients.length()").isEqualTo(1)
				.jsonPath("$.employees.length()").isEqualTo(1)
				.jsonPath("$.programs.length()").isEqualTo(1);
	}

	@Test
	public void getGymNotFound() {
		getAndVerifyGym(GYM_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/gym-composite/" + GYM_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + GYM_ID_NOT_FOUND);
	}

	@Test
	public void getGymInvalidInput() {
		getAndVerifyGym(GYM_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/gym-composite/" + GYM_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + GYM_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyGym(int gymId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/gym-composite/" + gymId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

}
