package se.magnus.microservices.core.program;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.program.*;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.program.persistence.ProgramRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.datasource.url=jdbc:h2:mem:program-db"})
class ProgramServiceApplicationTests {

    @Autowired
    private WebTestClient client;
    @Autowired
    private ProgramRepository repository;
    @Autowired
    private Sink channels;
    private AbstractMessageChannel input = null;


    @BeforeEach
    public void setupDb() {
        input = (AbstractMessageChannel) channels.input();

        repository.deleteAll();
    }

    @Test
    public void getProgramByGymId() {

        int gymId = 1;

        assertEquals(0, repository.findByGymId(gymId).size());

        sendCreateProgramEvent(gymId, 1);
        sendCreateProgramEvent(gymId, 2);
        sendCreateProgramEvent(gymId, 3);

        assertEquals(3, repository.findByGymId(gymId).size());

        postAndVerifyProgram(gymId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].gymId").isEqualTo(gymId)
                .jsonPath("$[2].programId").isEqualTo(3);
    }

    @Test
    public void deletePrograms() {

        int gymId = 1;
        int programId = 1;

        sendCreateProgramEvent(gymId, programId);
        assertEquals(1, repository.findByGymId(gymId).size());

        sendDeleteProgramEvent(gymId);
        assertEquals(0, repository.findByGymId(gymId).size());

        sendDeleteProgramEvent(gymId);
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

    private WebTestClient.BodyContentSpec getAndVerifyProgramsByGymId(String gymIdQuery, HttpStatus expectedStatus) {
        return client.get()
                .uri("/program" + gymIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyProgram(int gymId, HttpStatus expectedStatus) {
        return getAndVerifyProgramsByGymId("?gymId=" + gymId, expectedStatus);
    }

    private void sendCreateProgramEvent(int gymId, int programId) {
        Program program = new Program(gymId, programId, "Name " + programId, "SA");
        Event<Integer, Gym> event = new Event(CREATE, gymId, program);
        input.send(new GenericMessage<>(event));
    }

    private void sendDeleteProgramEvent(int gymId) {
        Event<Integer, Program> event = new Event(DELETE, gymId, null);
        input.send(new GenericMessage<>(event));
    }

}
