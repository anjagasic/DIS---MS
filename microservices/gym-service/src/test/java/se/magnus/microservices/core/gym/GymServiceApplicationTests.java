package se.magnus.microservices.core.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.gym.*;
import se.magnus.microservices.core.gym.persistence.GymRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT,
        properties = {"eureka.client.enabled=false", "spring.data.mongodb.port: 0"})
class GymServiceApplicationTests {
    @Autowired
    private WebTestClient client;
    @Autowired
    private GymRepository repository;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    public void getGymById() {
        int gymId = 1;
        assertNull(repository.findByGymId(gymId).block());
        assertEquals(0, (long)repository.count().block());

        sendCreateGymEvent(gymId);

        assertNotNull(repository.findByGymId(gymId).block());
        assertEquals(1, (long)repository.count().block());

        getAndVerifyGym(gymId, OK).jsonPath("$.gymId").isEqualTo(gymId);
    }

    @Test
    public void deleteGym() {
        int gymId = 1;
        sendCreateGymEvent(gymId);
        assertNotNull(repository.findByGymId(gymId).block());

        sendDeleteGymEvent(gymId);
        assertNull(repository.findByGymId(gymId).block());

        sendDeleteGymEvent(gymId);
    }

    @Test
    public void getGymInvalidParameterString() {

        client.get().uri("/gym/no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST).expectHeader().contentType(APPLICATION_JSON)
                .expectBody().jsonPath("$.path").isEqualTo("/gym/no-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getGymNotFound() {

        int gymIdNotFound = 113;

        getAndVerifyGym(String.valueOf(gymIdNotFound), NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/gym/" + gymIdNotFound)
                .jsonPath("$.message").isEqualTo("No gym found for gymId: " + gymIdNotFound);
    }

    @Test
    public void getGymInvalidParameterNegativeValue() {

        int gymIdInvalid = -1;

        getAndVerifyGym(String.valueOf(gymIdInvalid), UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/gym/" + gymIdInvalid)
                .jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyGym(int gymId, HttpStatus expectedStatus) {
        return getAndVerifyGym("/" + gymId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyGym(String gymIdPath, HttpStatus expectedStatus) {
        return client.get()
                .uri("/gym/" + gymIdPath)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private void sendCreateGymEvent(int gymId) {
        Gym gym = new Gym(gymId, "Name " + gymId, "address", "SA");

        //Event<Integer, Meal> event = new Event(CREATE, mealId, meal);
        //input.send(new GenericMessage<>(event));
    }

    private void sendDeleteGymEvent(int gymId) {
        //Event<Integer, Meal> event = new Event(DELETE, mealId, null);
        //input.send(new GenericMessage<>(event));
    }
}
