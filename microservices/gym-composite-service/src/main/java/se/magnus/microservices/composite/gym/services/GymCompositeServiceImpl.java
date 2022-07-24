package se.magnus.microservices.composite.gym.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.magnus.api.composite.gym.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.program.Program;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.employee.Employee;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GymCompositeServiceImpl implements GymCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(GymCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private GymCompositeIntegration integration;

    @Autowired
    public GymCompositeServiceImpl(ServiceUtil serviceUtil, GymCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public GymAggregate getGym(int gymId) {
        Gym gym = integration.getGym(gymId);
        if (gym == null) throw new NotFoundException("No gym found for gymId: " + gymId);

        List<Program> programs = integration.getPrograms(gymId);

        List<Client> clients = integration.getClients(gymId);

        List<Employee> employees = integration.getEmployees(gymId);

        LOG.debug("getCompositeGym: aggregate entity found for gymId: {}", gymId);

        return createGymAggregate(gym, programs, clients, employees, serviceUtil.getServiceAddress());
    }

    private GymAggregate createGymAggregate(Gym gym, List<Program> programs, List<Client> clients, List<Employee> employees, String serviceAddress) {

        // 1. Setup gym info
        int gymId = gym.getGymId();
        String name = gym.getName();
        String address = gym.getAddress();

        // 2. Copy summary program info, if available
        List<ProgramSummary> programSummaries = (programs == null) ? null :
                programs.stream()
                        .map(r -> new ProgramSummary(r.getProgramId(), r.getName()))
                        .collect(Collectors.toList());

        // 3. Copy summary client info, if available
        List<ClientSummary> clientSummaries = (clients == null) ? null :
                clients.stream()
                        .map(r -> new ClientSummary(r.getClientId(), r.getFullName(), r.getGender(), r.getAge()))
                        .collect(Collectors.toList());

        // 4. Copy summary employee info, if available
        List<EmployeeSummary> employeeSummaries = (employees == null) ? null :
                employees.stream()
                        .map(r -> new EmployeeSummary(r.getEmployeeId(), r.getFullName()))
                        .collect(Collectors.toList());

        // 5. Create info regarding the involved microservices addresses
        String gymAddress = gym.getServiceAddress();
        String clientAddress = (clients != null && clients.size() > 0) ? clients.get(0).getServiceAddress() : "";
        String programAddress = (programs != null && programs.size() > 0) ? programs.get(0).getServiceAddress() : "";
        String employeeAddress = (employees != null && employees.size() > 0) ? employees.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, gymAddress, clientAddress, programAddress, employeeAddress);

        return new GymAggregate(gymId, name, address, programSummaries, clientSummaries, employeeSummaries, serviceAddresses);
    }

    @Override
    public void createCompositeGym(GymAggregate body) {
        try {

            LOG.debug("createCompositeGym: creates a new composite entity for gymId: {}", body.getGymId());

            Gym gym = new Gym(body.getGymId(), body.getName(), body.getAddress(), null);
            integration.createGym(gym);

            if (body.getClients() != null) {
                body.getClients().forEach(clientSummary -> {
                    Client client = new Client(body.getGymId(), clientSummary.getClientId(), clientSummary.getFullName(),
                            clientSummary.getGender(), clientSummary.getAge(), null);
                    integration.createClient(client);
                });
            }

            if (body.getEmployees() != null) {
                body.getEmployees().forEach(employeeSummary -> {
                    Employee employee = new Employee(body.getGymId(), employeeSummary.getEmployeeId(), employeeSummary.getFullName(), null);
                    integration.createEmployee(employee);
                });
            }

            if (body.getPrograms() != null) {
                body.getPrograms().forEach(programSummary -> {
                    Program program = new Program(body.getGymId(), programSummary.getProgramId(), programSummary.getName(), null);
                    integration.createProgram(program);
                });
            }

            LOG.debug("createCompositeGym: composite entites created for gymId: {}", body.getGymId());
        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }

    }

    @Override
    public void deleteCompositeGym(int gymId) {
        LOG.debug("deleteCompositeGym: Deletes a gym aggregate for gymId: {}", gymId);

        integration.deleteGym(gymId);
        integration.deleteClients(gymId);
        integration.deleteEmployees(gymId);
        integration.deletePrograms(gymId);

        LOG.debug("getCompositeGym: aggregate entities deleted for gymId: {}", gymId);
    }
}