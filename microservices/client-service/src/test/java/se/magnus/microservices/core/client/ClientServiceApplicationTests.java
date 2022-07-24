package se.magnus.microservices.core.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.client.*;
import se.magnus.microservices.core.client.persistence.ClientRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
class ClientServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ClientRepository repository;

	@BeforeEach
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getClientByGymId() {

		int gymId = 1;

		postAndVerifyClient(gymId, 1, OK);
		postAndVerifyClient(gymId, 2, OK);
		postAndVerifyClient(gymId, 3, OK);

		assertEquals(3, repository.findByGymId(gymId).size());
	}

	@Test
	public void deleteClients() {

		int gymId = 1;
		int clientId = 1;

		postAndVerifyClient(gymId, clientId, OK);
		assertEquals(1, repository.findByGymId(gymId).size());

		deleteAndVerifyClientsByGymId(gymId, OK);
		assertEquals(0, repository.findByGymId(gymId).size());

		deleteAndVerifyClientsByGymId(gymId, OK);
	}

	@Test
	public void getClientMissingParameter() {

		getAndVerifyClientsByGymId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
	}

	@Test
	public void getClientInvalidParameter() {

		getAndVerifyClientsByGymId("?gymId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getClientNotFound() {

		getAndVerifyClientsByGymId("?gymId=113", OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getClientInvalidParameterNegativeValue() {

		int gymIdInvalid = -1;

		getAndVerifyClientsByGymId("?gymId=" + gymIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/client")
				.jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
	}

	@Test
	public void postClient() {
		int gymId = 1;
		int clientId = 1;
		postAndVerifyClient(gymId, clientId, OK); //TODO
		assertNotNull(repository.findByClientId(clientId));
	}

	private WebTestClient.BodyContentSpec postAndVerifyClient(int gymId, int clientId, HttpStatus expectedStatus) {
		Client clientObj = new Client(gymId, clientId, "Name 1", "Male", "20", "SA");
		return client.post()
				.uri("/client")
				.body(just(clientObj), Client.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyClientsByGymId(String gymIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/client" + gymIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyClientsByGymId(int gymId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/client?gymId=" + gymId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
