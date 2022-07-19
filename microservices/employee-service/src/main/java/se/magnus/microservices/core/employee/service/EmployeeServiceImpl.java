package se.magnus.microservices.core.employee.service;

import org.springframework.dao.DataIntegrityViolationException;
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
    public List<Employee> getEmployees(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        List<EmployeeEntity> entityList = repository.findByGymId(gymId);
        List<Employee> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getEmployees: response size: {}", list.size());

        return list;
    }

    @Override
    public Employee createEmployee(Employee body) {
        try {
            EmployeeEntity entity = mapper.apiToEntity(body);
            EmployeeEntity newEntity = repository.save(entity);

            LOG.debug("createEmployee: created a employee entity: {}/{}", body.getGymId(), body.getEmployeeId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dke) {
            throw new InvalidInputException("Duplicate key, gym Id: " + body.getGymId() + ", employee Id:" + body.getEmployeeId());
        }
    }

    @Override
    public void deleteEmployees(int gymId) {
        LOG.debug("deleteEmployees: tries to delete employee for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId));
    }
}
