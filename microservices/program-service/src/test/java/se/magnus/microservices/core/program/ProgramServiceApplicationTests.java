package se.magnus.microservices.core.program;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.program.*;
import se.magnus.microservices.core.program.persistence.ProgramRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.datasource.url=jdbc:h2:mem:review-db"})
class ProgramServiceApplicationTests {

    @Autowired
    private WebTestClient client;
    @Autowired
    private ProgramRepository repository;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void getProgramByGymId() {

        int gymId = 1;

        postAndVerifyProgram(gymId, 1, OK);
        postAndVerifyProgram(gymId, 2, OK);
        postAndVerifyProgram(gymId, 3, OK);

        assertEquals(3, repository.findByGymId(gymId).size());
    }

    @Test
    public void deletePrograms() {

        int gymId = 1;
        int programId = 1;

        postAndVerifyProgram(gymId, programId, OK);
        assertEquals(1, repository.findByGymId(gymId).size());

        deleteAndVerifyProgramsByGymId(gymId, OK);
        assertEquals(0, repository.findByGymId(gymId).size());

        deleteAndVerifyProgramsByGymId(gymId, OK);
    }

    @Test
    public void getProgramMissingParameter() {

        getAndVerifyProgramsByGymId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Required int parameter 'gymId' is not present");
    }

    @Test
    public void getProgramInvalidParameter() {

        getAndVerifyProgramsByGymId("?gymId=no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getProgramNotFound() {

        getAndVerifyProgramsByGymId("?gymId=113", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getProgramInvalidParameterNegativeValue() {

        int gymIdInvalid = -1;

        getAndVerifyProgramsByGymId("?gymId=" + gymIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/program")
                .jsonPath("$.message").isEqualTo("Invalid gymId: " + gymIdInvalid);
    }

    @Test
    public void postProgram() {
        int gymId = 1;
        int programId = 1;
        postAndVerifyProgram(gymId, programId, OK);
        assertNotNull(repository.findByProgramId(programId));
    }

    private WebTestClient.BodyContentSpec getAndVerifyProgramsByGymId(String gymIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/program" + gymIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyProgram(int gymId, int programId, HttpStatus expectedStatus) {
        Program program = new Program(gymId, programId, "name 1", "SA");
        return client.post()
                .uri("/program")
                .body(just(program), Program.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyProgramsByGymId(int gymId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("/program?gymId=" + gymId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus).
                expectBody();
    }
}
