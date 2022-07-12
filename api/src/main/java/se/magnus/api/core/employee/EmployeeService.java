package se.magnus.api.core.employee;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EmployeeService {

    /**
     * Sample usage: curl $HOST:$PORT/employee?gymId=1
     *
     * @param gymId
     * @return
     */
    @GetMapping(
        value    = "/employee",
        produces = "application/json")
    List<Employee> getEmployees(@RequestParam(value = "gymId", required = true) int gymId);
}
