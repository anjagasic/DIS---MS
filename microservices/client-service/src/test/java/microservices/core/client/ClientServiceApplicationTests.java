package microservices.core.client;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
class ClientServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getClientByGymId() {

		int gymId = -1;

		client.get()
				.uri("/client?gymId=" + gymId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(1)
				.jsonPath("$[0].gymId").isEqualTo(gymId);
	}

	@Test
	public void getClientMissingParameter() {

		client.get()
				.uri("/client")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
	}

	@Test
	public void getClientInvalidParameter() {

		client.get()
				.uri("/client?gymId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getClientNotFound() {

		int gymIdNotFound = 113;

		client.get()
				.uri("/client?gymId=" + gymIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(6);
	}

	@Test
	public void getClientInvalidParameterNegativeValue() {

		int gymIdInvalid = -1;

		client.get()
				.uri("/client?gymId=" + gymIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
	}

}