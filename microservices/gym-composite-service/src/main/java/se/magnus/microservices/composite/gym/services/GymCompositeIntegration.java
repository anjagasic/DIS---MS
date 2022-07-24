package se.magnus.microservices.composite.gym.services;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import se.magnus.api.core.client.ClientService;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class GymCompositeIntegration implements GymService, ProgramService, ClientService, EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(GymCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String gymServiceUrl;
    private final String programServiceUrl;
    private final String clientServiceUrl;
    private final String employeeServiceUrl;

    @Autowired
    public GymCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.gym-service.host}") String gymServiceHost,
            @Value("${app.gym-service.port}") int gymServicePort,
            @Value("${app.program-service.host}") String programServiceHost,
            @Value("${app.program-service.port}") int programServicePort,
            @Value("${app.client-service.host}") String clientServiceHost,
            @Value("${app.client-service.port}") int clientServicePort,
            @Value("${app.employee-service.host}") String employeeCreditServiceHost,
            @Value("${app.employee-service.port}") int employeeCreditServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        gymServiceUrl = "http://" + gymServiceHost + ":" + gymServicePort + "/gym/";
        programServiceUrl = "http://" + programServiceHost + ":" + programServicePort + "/program";
        clientServiceUrl = "http://" + clientServiceHost + ":" + clientServicePort + "/client";
        employeeServiceUrl = "http://" + employeeCreditServiceHost + ":" + employeeCreditServicePort + "/employee";
    }

    public Gym getGym(int gymId) {

        try {
            String url = gymServiceUrl + "/" + gymId;
            LOG.debug("Will call getGym API on URL: {}", url);

            Gym gym = restTemplate.getForObject(url, Gym.class);
            LOG.debug("Found a gym with id: {}", gym.getGymId());

            return gym;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Gym createGym(Gym body) {
        try {
            String url = gymServiceUrl;
            LOG.debug("Will post a new gym to URL: {}", url);

            Gym gym = restTemplate.postForObject(url, body, Gym.class);
            LOG.debug("Created a gym with id: {}", gym.getGymId());

            return gym;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteGym(int gymId) {
        try {
            String url = gymServiceUrl + "/" + gymId;
            LOG.debug("Will call the deleteGym API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Program> getPrograms(int gymId) {
        try {
            String url = programServiceUrl + "?gymId=" + gymId;

            LOG.debug("Will call getPrograms API on URL: {}", url);
            List<Program> programs = restTemplate.exchange(
                    url,
                    GET,
                    null,
                    new ParameterizedTypeReference<List<Program>>() {
                    }
            ).getBody();

            LOG.debug("Found {} programs for gym with id: {}", programs.size(), gymId);
            return programs;
        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting programs, return zero programs: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Program createProgram(Program body) {
        try {
            String url = programServiceUrl;
            LOG.debug("Will post a new program to URL: {}", url);

            Program program = restTemplate.postForObject(url, body, Program.class);
            LOG.debug("Created a program with id: {}", program.getProgramId());

            return program;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deletePrograms(int gymId) {
        try {
            String url = programServiceUrl + "?gymId=" + gymId;
            LOG.debug("Will call the deletePrograms() API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Client> getClients(int gymId) {
        try {
            String url = clientServiceUrl + "?gymId=" + gymId;

            LOG.debug("Will call getClients API on URL: {}", url);
            List<Client> clients = restTemplate.exchange(
                    url,
                    GET,
                    null,
                    new ParameterizedTypeReference<List<Client>>() {}
            ).getBody();

            LOG.debug("Found {} clients for gym with id: {}", clients.size(), gymId);
            return clients;
        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting clients, return zero clients: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Client createClient(Client body) {
        try {
            String url = clientServiceUrl;
            LOG.debug("Will post a new client to URL: {}", url);

            Client client = restTemplate.postForObject(url, body, Client.class);
            LOG.debug("Created a client with id: {}", client.getClientId());

            return client;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteClients(int gymId) {
        try {
            String url = clientServiceUrl + "?gymId=" + gymId;
            LOG.debug("Will call the deleteClients() API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Employee> getEmployees(int gymId) {
        try {
            String url = employeeServiceUrl + "?gymId=" + gymId;

            LOG.debug("Will call getEmployees API on URL: {}", url);
            List<Employee> employees = restTemplate.exchange(
                    url,
                    GET,
                    null,
                    new ParameterizedTypeReference<List<Employee>>() {}
            ).getBody();

            LOG.debug("Found {} employees for gym with id: {}", employees.size(), gymId);
            return employees;
        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting employees, return zero employees: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Employee createEmployee(Employee body) {
        try {
            String url = employeeServiceUrl;
            LOG.debug("Will post a new employee to URL: {}", url);

            Employee employee = restTemplate.postForObject(url, body, Employee.class);
            LOG.debug("Created a employee with id: {}", employee.getEmployeeId());

            return employee;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteEmployees(int gymId) {
        try {
            String url = employeeServiceUrl + "?gymId=" + gymId;
            LOG.debug("Will call the deleteEmployees() API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}