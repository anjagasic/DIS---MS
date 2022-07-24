package se.magnus.api.composite.gym;

import java.util.List;

public class GymAggregate {
    private final int gymId;
    private final String name;
    private final String address;
    private final List<ProgramSummary> programs;
    private final List<ClientSummary> clients;
    private final List<EmployeeSummary> employees;
    private final ServiceAddresses serviceAddresses;

    public GymAggregate() {
        this.gymId = 0;
        this.name = null;
        this.address = null;
        this.programs = null;
        this.clients = null;
        this.employees = null;
        this.serviceAddresses = null;
    }

    public GymAggregate(
            int gymId,
            String name,
            String address,
            List<ProgramSummary> programs,
            List<ClientSummary> clients,
            List<EmployeeSummary> employees,
            ServiceAddresses serviceAddresses
    ) {
        this.gymId = gymId;
        this.name = name;
        this.address = address;
        this.programs = programs;
        this.clients = clients;
        this.employees = employees;
        this.serviceAddresses = serviceAddresses;
    }

    public int getGymId() {
        return gymId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<ProgramSummary> getPrograms() {
        return programs;
    }

    public List<ClientSummary> getClients() {
        return clients;
    }

    public List<EmployeeSummary> getEmployees() {
        return employees;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }
}
