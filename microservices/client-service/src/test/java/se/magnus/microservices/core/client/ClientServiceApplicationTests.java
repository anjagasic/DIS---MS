package se.magnus.microservices.core.client;

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
import se.magnus.api.core.client.*;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.client.persistence.ClientRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {
		"eureka.client.enabled=false",
		"spring.data.mongodb.port: 0"
})
class ClientServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ClientRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@BeforeEach
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getClientByGymId() {
		int gymId = 1;

		sendCreateClientEvent(gymId, 1);
		sendCreateClientEvent(gymId, 2);
		sendCreateClientEvent(gymId, 3);

		assertEquals(3, (long) repository.findByGymId(gymId).count().block());
		postAndVerifyClient(gymId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].gymId").isEqualTo(gymId)
				.jsonPath("$[2].clientId").isEqualTo(3);
	}

	@Test
	public void deleteClients() {
		int gymId = 1;
		int clientId = 1;

		sendCreateClientEvent(gymId, clientId);
		assertEquals(1, (long) repository.findByGymId(gymId).count().block());

		sendDeleteClientEvent(gymId);
		assertEquals(0, (long)repository.findByGymId(gymId).count().block());

		sendDeleteClientEvent(gymId);
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

	private WebTestClient.BodyContentSpec postAndVerifyClient(int gymId, HttpStatus expectedStatus) {
		return getAndVerifyClientsByGymId("?gymId=" + gymId, expectedStatus);
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

	private void sendCreateClientEvent(int gymId, int clientId) {
		Client client = new Client(gymId, clientId, "name " + clientId, "Female", "30", "SA");
		Event<Integer, Gym> event = new Event(CREATE, gymId, client);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteClientEvent(int gymId) {
		Event<Integer, Gym> event = new Event(DELETE, gymId, null);
		input.send(new GenericMessage<>(event));
	}
}
