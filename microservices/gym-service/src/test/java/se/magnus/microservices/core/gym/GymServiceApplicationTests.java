package se.magnus.microservices.core.gym;

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
@SpringBootTest(webEnvironment = RANDOM_PORT)
class GymServiceApplicationTests {
    @Autowired
    private WebTestClient client;

    @Test
    public void getGymById() {

        int gymId = 1;

        client.get().uri("/gym/" + gymId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON).expectBody()
                .jsonPath("$.gymId").isEqualTo(gymId);
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

        client.get().uri("/gym/" + gymIdNotFound)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON).expectBody()
                .jsonPath("$.path").isEqualTo("/gym/" + gymIdNotFound)
                .jsonPath("$.message").isEqualTo("No gym found for gymId: " + gymIdNotFound);
    }

    @Test
    public void getGymInvalidParameterNegativeValue() {

        int gymIdInvalid = -1;

        client.get().uri("/gym/" + gymIdInvalid).accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON).expectBody()
                .jsonPath("$.path").isEqualTo("/gym/" + gymIdInvalid)
                .jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);

    }
}
