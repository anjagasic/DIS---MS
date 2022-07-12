package se.magnus.microservices.core.employee;

import se.magnus.api.core.employee.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public EmployeeServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Employee> getEmployees(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        if (gymId == 113) {
            LOG.debug("No employee found for gymId: {}", gymId);
            return new ArrayList<>();
        }

        List<Employee> list = new ArrayList<>();
        list.add(new Employee(gymId, 1, "Anja Gasic", serviceUtil.getServiceAddress()));
        list.add(new Employee(gymId, 2, "Nenad Ralic", serviceUtil.getServiceAddress()));
        list.add(new Employee(gymId, 3, "Igor Savic", serviceUtil.getServiceAddress()));

        LOG.debug("/employee response size: {}", list.size());

        return list;
    }
}
