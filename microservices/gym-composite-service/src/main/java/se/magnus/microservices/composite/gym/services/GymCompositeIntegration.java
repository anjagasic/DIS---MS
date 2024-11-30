package se.magnus.microservices.composite.gym.services;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.gym.GymService;
import se.magnus.api.core.program.Program;
import se.magnus.api.core.program.ProgramService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;

import se.magnus.api.core.client.ClientService;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static reactor.core.publisher.Flux.empty;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@EnableBinding(GymCompositeIntegration.MessageSources.class)
@Component
public class GymCompositeIntegration implements GymService, ProgramService, ClientService, EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(GymCompositeIntegration.class);

    private final String gymServiceUrl = "http://gym";
    private final String clientServiceUrl = "http://client";
    private final String employeeServiceUrl = "http://employee";
    private final String programServiceUrl = "http://program";

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;
    private final ObjectMapper mapper;

    private MessageSources messageSources;

    public interface MessageSources {

        String OUTPUT_GYMS = "output-gyms";
        String OUTPUT_PROGRAMS = "output-programs";
        String OUTPUT_CLIENTS = "output-clients";
        String OUTPUT_EMPLOYEES = "output-employees";

        @Output(OUTPUT_GYMS)
        MessageChannel outputGyms();

        @Output(OUTPUT_CLIENTS)
        MessageChannel outputClients();

        @Output(OUTPUT_EMPLOYEES)
        MessageChannel outputEmployees();

        @Output(OUTPUT_PROGRAMS)
        MessageChannel outputPrograms();
    }

    @Autowired
    public GymCompositeIntegration(
            WebClient.Builder webClientBuilder,
            MessageSources messageSources,
            ObjectMapper mapper
    ) {
        this.messageSources = messageSources;
        this.webClientBuilder = webClientBuilder;
        this.mapper = mapper;
    }

    @Override
    public Mono<Gym> getGym(int gymId) {
        String url = gymServiceUrl + "/gym/" + gymId;
        LOG.debug("Will call getGym API on URL: {}", url);

        return getWebClient().get().uri(url).retrieve()
                .bodyToMono(Gym.class)
                .log()
                .onErrorMap(
                        WebClientResponseException.class,
                        ex -> handleException(ex)
                );
    }

    @Override
    public Gym createGym(Gym body) {
        messageSources.outputGyms().send(MessageBuilder.withPayload(new Event(CREATE, body.getGymId(), body)).build());
        return body;
    }

    @Override
    public void deleteGym(int gymId) {
        messageSources.outputGyms().send(MessageBuilder.withPayload(new Event(DELETE, gymId, null)).build());
    }

    @Override
    public Flux<Program> getPrograms(int gymId) {
        String url = programServiceUrl + "/program?gymId=" + gymId;
        LOG.debug("Will call getPrograms API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve()
                .bodyToFlux(Program.class)
                .log().onErrorResume(error -> empty());
    }

    @Override
    public Program createProgram(Program body) {
        messageSources.outputPrograms().send(MessageBuilder.withPayload(new Event(CREATE, body.getGymId(), body)).build());
        return body;
    }

    @Override
    public void deletePrograms(int gymId) {
        messageSources.outputPrograms().send(MessageBuilder.withPayload(new Event(DELETE, gymId, null)).build());
    }

    @Override
    public Flux<Client> getClients(int gymId) {
        String url = clientServiceUrl + "/client?gymId=" + gymId;
        LOG.debug("Will call getClients API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve()
                .bodyToFlux(Client.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Client createClient(Client body) {
        messageSources.outputClients().send(MessageBuilder.withPayload(new Event(CREATE, body.getGymId(), body)).build());
        return body;
    }

    @Override
    public void deleteClients(int gymId) {
        messageSources.outputClients().send(MessageBuilder.withPayload(new Event(DELETE, gymId, null)).build());
    }

    public Flux<Employee> getEmployees(int gymId) {
        String url = employeeServiceUrl + "/employee?gymId=" + gymId;

        LOG.debug("Will call getEmployees API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve()
                .bodyToFlux(Employee.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Employee createEmployee(Employee body) {
        messageSources.outputEmployees().send(MessageBuilder.withPayload(new Event(CREATE, body.getGymId(), body)).build());
        return body;
    }

    @Override
    public void deleteEmployees(int gymId) {
        messageSources.outputEmployees().send(MessageBuilder.withPayload(new Event(DELETE, gymId, null)).build());
    }

    public Mono<Health> getProgramHealth() {
        return getHealth(programServiceUrl);
    }

    public Mono<Health> getClientHealth() {
        return getHealth(clientServiceUrl);
    }

    public Mono<Health> getEmployeeHealth() {
        return getHealth(employeeServiceUrl);
    }

    public Mono<Health> getGymHealth() {
        return getHealth(gymServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }


    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (wcre.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));
            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(wcre));
            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}