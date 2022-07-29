package se.magnus.microservices.core.employee.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.employee.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;
import se.magnus.microservices.core.employee.persistence.EmployeeRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final EmployeeMapper mapper;
    private final EmployeeRepository repository;

    @Autowired
    public EmployeeServiceImpl(ServiceUtil serviceUtil, EmployeeMapper mapper, EmployeeRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Flux<Employee> getEmployees(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        return repository.findByGymId(gymId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public Employee createEmployee(Employee body) {
        if (body.getGymId() < 1) throw new InvalidInputException("Invalid gymId: " + body.getGymId());
        EmployeeEntity entity = mapper.apiToEntity(body);
        Mono<Employee> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Gym Id: " + body.getGymId() + ", Employee Id:" + body.getEmployeeId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public void deleteEmployees(int gymId) {
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        LOG.debug("deleteEmployees: tries to delete employee for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId)).block();
    }
}
