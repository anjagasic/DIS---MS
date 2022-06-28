package microservices.composite.gym;

import api.core.client.Client;
import api.core.employee.Employee;
import api.core.gym.Gym;
import api.core.program.Program;
import microservices.composite.gym.services.GymCompositeIntegration;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;

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

	@Before
	public void setUp() {

		when(compositeIntegration.getGym(GYM_ID_OK)).
				thenReturn(new Gym(GYM_ID_OK, "Test Name", "Test address", "mock-address"));
		when(compositeIntegration.getClients(GYM_ID_OK)).
				thenReturn(singletonList(new Client(GYM_ID_OK, 1, "Test full name", "Test gender", "Test age", "mock address")));
		when(compositeIntegration.getEmployees(GYM_ID_OK)).
				thenReturn(singletonList(new Employee(GYM_ID_OK, 1, "Test full name", "mock address")));
		when(compositeIntegration.getPrograms(GYM_ID_OK)).
				thenReturn(singletonList(new Program(GYM_ID_OK, 1, "Test name", "mock address")));

		when(compositeIntegration.getGym(GYM_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + GYM_ID_NOT_FOUND));

		when(compositeIntegration.getGym(GYM_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + GYM_ID_INVALID));
	}

	@Test
	public void getGymById() {

		client.get()
				.uri("/gym-composite/" + GYM_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.gymId").isEqualTo(GYM_ID_OK)
				.jsonPath("$.clients.length()").isEqualTo(1)
				.jsonPath("$.employees.length()").isEqualTo(1)
				.jsonPath("$.programs.length()").isEqualTo(1);
	}

	@Test
	public void getGymNotFound() {

		client.get()
				.uri("/gym-composite/" + GYM_ID_NOT_FOUND)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/gym-composite/" + GYM_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + GYM_ID_NOT_FOUND);
	}

	@Test
	public void getGymInvalidInput() {

		client.get()
				.uri("/gym-composite/" + GYM_ID_INVALID)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/gym-composite/" + GYM_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + GYM_ID_INVALID);
	}

}
