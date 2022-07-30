package se.magnus.microservices.composite.gym;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.composite.gym.ClientSummary;
import se.magnus.api.composite.gym.EmployeeSummary;
import se.magnus.api.composite.gym.GymAggregate;
import se.magnus.api.composite.gym.ProgramSummary;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.program.Program;
import se.magnus.api.event.Event;
import se.magnus.microservices.composite.gym.services.GymCompositeIntegration;

import java.util.concurrent.BlockingQueue;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import static se.magnus.microservices.composite.gym.IsSameEvent.sameEventExceptCreatedAt;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)

public class MessagingTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private GymCompositeIntegration.MessageSources channels;

    @Autowired
    private MessageCollector collector;

    BlockingQueue<Message<?>> queueGyms = null;
    BlockingQueue<Message<?>> queueClients = null;
    BlockingQueue<Message<?>> queueEmployees = null;
    BlockingQueue<Message<?>> queuePrograms = null;

    @Before
    public void setUp() {
        queueGyms = getQueue(channels.outputGyms());
        queueClients = getQueue(channels.outputClients());
        queueEmployees = getQueue(channels.outputEmployees());
        queuePrograms = getQueue(channels.outputPrograms());
    }

    @Test
    public void createCompositeGym1() {

        GymAggregate composite = new GymAggregate(1, "name", "address", null, null, null, null);
        postAndVerifyGym(composite, OK);

        // Assert one expected new gym events queued up
        assertEquals(1, queueGyms.size());

        Event<Integer, Gym> expectedEvent = new Event(CREATE, composite.getGymId(), new Gym(composite.getGymId(), composite.getName(), composite.getAddress(), null));
        assertThat(queueGyms, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert none client, employee and program events
        assertEquals(0, queueClients.size());
        assertEquals(0, queueEmployees.size());
        assertEquals(0, queuePrograms.size());
    }

    @Test
    public void createCompositeGym2() {

        GymAggregate composite = new GymAggregate(1, "name", "address",
                singletonList(new ProgramSummary(1, "program")),
                singletonList(new ClientSummary(1, "full name", "male", "25")),
                singletonList(new EmployeeSummary(1, "full name employee")), null);

        postAndVerifyGym(composite, OK);

        // Assert one create gym event queued up
        assertEquals(1, queueGyms.size());

        Event<Integer, Gym> expectedGymEvent = new Event(CREATE, composite.getGymId(), new Gym(composite.getGymId(), composite.getName(), composite.getAddress(), null));
        assertThat(queueGyms, receivesPayloadThat(sameEventExceptCreatedAt(expectedGymEvent)));

        // Assert one create client event queued up
        assertEquals(1, queueClients.size());

        ClientSummary cli = composite.getClients().get(0);
        Event<Integer, Gym> expectedClientEvent = new Event(CREATE, composite.getGymId(), new Client(composite.getGymId(), cli.getClientId(), cli.getFullName(), cli.getGender(), cli.getAge(), null));
        assertThat(queueClients, receivesPayloadThat(sameEventExceptCreatedAt(expectedClientEvent)));

        // Assert one create employee event queued up
        assertEquals(1, queueEmployees.size());

        EmployeeSummary emp = composite.getEmployees().get(0);
        Event<Integer, Gym> expectedEmployeeEvent = new Event(CREATE, composite.getGymId(), new Employee(composite.getGymId(), emp.getEmployeeId(), emp.getFullName(), null));
        assertThat(queueEmployees, receivesPayloadThat(sameEventExceptCreatedAt(expectedEmployeeEvent)));

        // Assert one create program event queued up
        assertEquals(1, queuePrograms.size());

        ProgramSummary pro = composite.getPrograms().get(0);
        Event<Integer, Gym> expectedProgramEvent = new Event(CREATE, composite.getGymId(), new Program(composite.getGymId(), pro.getProgramId(), pro.getName(), null));
        assertThat(queuePrograms, receivesPayloadThat(sameEventExceptCreatedAt(expectedProgramEvent)));
    }

    @Test
    public void deleteCompositeGym() {

        deleteAndVerifyGym(1, OK);

        // Assert one delete gym event queued up
        assertEquals(1, queueGyms.size());

        Event<Integer, Gym> expectedEvent = new Event(DELETE, 1, null);
        assertThat(queueGyms, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert one delete client event queued up
        assertEquals(1, queueClients.size());

        Event<Integer, Gym> expectedClientEvent = new Event(DELETE, 1, null);
        assertThat(queueClients, receivesPayloadThat(sameEventExceptCreatedAt(expectedClientEvent)));

        // Assert one delete employee event queued up
        assertEquals(1, queueEmployees.size());

        Event<Integer, Gym> expectedEmployeeEvent = new Event(DELETE, 1, null);
        assertThat(queueEmployees, receivesPayloadThat(sameEventExceptCreatedAt(expectedEmployeeEvent)));

        // Assert one delete program event queued up
        assertEquals(1, queuePrograms.size());

        Event<Integer, Gym> expectedProgramEvent = new Event(DELETE, 1, null);
        assertThat(queuePrograms, receivesPayloadThat(sameEventExceptCreatedAt(expectedProgramEvent)));
    }

    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    private void postAndVerifyGym(GymAggregate compositeGym, HttpStatus expectedStatus) {
        client.post()
                .uri("/gym-composite")
                .body(just(compositeGym), GymAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyGym(int gymId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/gym-composite/" + gymId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}