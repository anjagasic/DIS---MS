package se.magnus.microservices.core.program;

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
class ProgramServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Test
    public void getProgramByGymId() {

        int gymId = 1;

        client.get()
                .uri("/program?gymId=" + gymId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].gymId").isEqualTo(gymId);
    }

    @Test
    public void getProgramMissingParameter() {

        client.get()
                .uri("/program")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
    }

    @Test
    public void getProgramInvalidParameter() {

        client.get()
                .uri("/program?gymId=no-integer")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getProgramNotFound() {

        int gymIdNotFound = 113;

        client.get()
                .uri("/program?gymId=" + gymIdNotFound)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getProgramInvalidParameterNegativeValue() {

        int gymIdInvalid = -1;

        client.get()
                .uri("/program?gymId=" + gymIdInvalid)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
    }
}
