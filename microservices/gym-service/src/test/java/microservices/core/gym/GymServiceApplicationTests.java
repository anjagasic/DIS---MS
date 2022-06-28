package microservices.core.gym;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class GymServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getEmployeeByGymId() {

		int gymId = 1;

		client.get()
				.uri("/employee?gymId=" + gymId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].gymId").isEqualTo(gymId);
	}

	@Test
	public void getEmployeeMissingParameter() {

		client.get()
				.uri("/employee")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/employee")
				.jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
	}

	@Test
	public void getEmployeeInvalidParameter() {

		client.get()
				.uri("/employee?gymId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/employee")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getEmployeeNotFound() {

		int gymIdNotFound = 113;

		client.get()
				.uri("/employee?gymId=" + gymIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getEmployeeInvalidParameterNegativeValue() {

		int gymIdInvalid = -1;

		client.get()
				.uri("/employee?gymId=" + gymIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/employee")
				.jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
	}
}
